package com.example.MATE.repository;

import com.example.MATE.model.Meeting;
import com.example.MATE.model.MeetingParticipant;
import com.example.MATE.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// 한 회의에 참여한 사람을 모두 가져옴
public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Integer> {
    @Query(value = "SELECT * FROM meeting_participant WHERE meeting_id = ?1", nativeQuery = true) // 함수의 첫번째 매개변수 (meetingId) 를 ?1 에 매핑
    List<MeetingParticipant> findParticipantsByMeetingId(Integer meetingId);
    
    //특정회의에 유저의 참여 여부 확인
    boolean existsByMeetingAndUser(Meeting meeting, User user);

    List<MeetingParticipant> findByMeeting_MeetingId(Integer meetingId);

    Optional<MeetingParticipant> findByMeeting_MeetingIdAndUser_UserId(Integer meetingId, Integer userId);

    List<MeetingParticipant> findByMeeting_MeetingIdAndIsAttendingTrue(Integer meetingId);
}
