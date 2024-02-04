package study.datajpa.dto;

import lombok.Getter;
import lombok.Setter;
import study.datajpa.entity.Member;

@Getter
@Setter
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        if (member.getTeam() != null) {
            this.teamName = member.getTeam().getName();
        }
    }
}
