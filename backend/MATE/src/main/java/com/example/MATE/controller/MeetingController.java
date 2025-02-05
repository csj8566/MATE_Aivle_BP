package com.example.MATE.controller;

import com.example.MATE.dto.MeetingDto;
import com.example.MATE.model.Meeting;
import com.example.MATE.model.MeetingParticipant;
import com.example.MATE.model.ScreenData;
import com.example.MATE.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("/create")
    public String createMeeting(@ModelAttribute MeetingDto meetingDto,
                                @RequestParam String mode,
                                @RequestParam Integer userId,
                                RedirectAttributes redirectAttributes) {
        System.out.println("[MeetingController/createMeeting] 실행!");
        try {
            MeetingDto savedMeeting = meetingService.createMeeting(meetingDto, userId);

            if ("host".equals(mode)) {
                return "redirect:/meeting/host/" + savedMeeting.getMeetingId() + "?userId=" + userId;
            } else {
                return "redirect:/meeting/client/" + savedMeeting.getMeetingId() + "?userId=" + userId;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회의 생성 중 오류 발생: " + e.getMessage());
            return "redirect:/user/userMain";
        }
    }

    @GetMapping("/host/{meetingId}")
    public String meetingRecorder(@PathVariable("meetingId") Integer meetingId, Model model){
        System.out.println("[MeetingController] 하이 호스트입니다.");
        //참여자와 createAt 가져오기--url의 meetingId가져다 meeting과 meetingParticipant에서 조회하면됨
        Meeting meeting = meetingService.getMeetingByMeetingId(meetingId);
        model.addAttribute("meetingParticipants",meeting.getMeetingParticipants());
        model.addAttribute("meetingName",meeting.getMeetingName());
        model.addAttribute("meetingCreatedAt",meeting.getCreatedAt());
        System.out.println(">>> : "+meeting.getMeetingName());
        return "meeting/host";
    }

    @GetMapping("/client/{meetingId}")
    public String meetingUser(@PathVariable("meetingId") Integer meetingId, Model model){
        System.out.println("[MeetingController] 하이 클라이언트입니다.");
        //참여자와 createAt 가져오기? 여기도 필요한가
        Meeting meeting = meetingService.getMeetingByMeetingId(meetingId);
        model.addAttribute("meetingParticipants",meeting.getMeetingParticipants());
        model.addAttribute("meetingName",meeting.getMeetingName());
        model.addAttribute("meetingCreatedAt",meeting.getCreatedAt());
        System.out.println(">>> : "+meeting.getMeetingName());
        return "meeting/client";
    }

    @PostMapping("/user/meetingInfo")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<MeetingDto>> getMeetingInfoByUserId(@RequestBody Map<String, Object> request) {
        System.out.println(">>> [MeetingController/getMeetingInfoByUserId] request: " + request);

        if (!request.containsKey("userId")) {
            throw new IllegalArgumentException("userId가 요청에 없음");
        }

        Integer userId;
        try {
            userId = Integer.parseInt(request.get("userId").toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("userId는 숫자여야 합니다.");
        }

        List<Integer> meetingIds = meetingService.getMeetingIdsByUserId(userId);
        List<MeetingDto> meetingInfoList = meetingIds.stream()
                .map(meetingService::getMeetingByMeetingId)
                .map(MeetingDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(meetingInfoList);
    }
}