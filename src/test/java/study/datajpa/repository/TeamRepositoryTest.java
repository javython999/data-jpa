package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TeamRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Test
    @DisplayName("Team을 저장 할 수 있다.")
    void save() {
        // given
        Team team = new Team("TeamA");

        // when
        Team savedTeam = teamRepository.save(team);
        Team findTeam = teamRepository.findById(savedTeam.getId()).get();

        // then
        assertThat(findTeam.getId()).isEqualTo(team.getId());
        assertThat(findTeam.getName()).isEqualTo(team.getName());
        assertThat(findTeam).isEqualTo(team);

    }


    @Test
    @DisplayName("Team을 단건 조회를 할 수 있다.")
    void findById() {
        // given
        Team team1 = new Team("team1");
        Team team2 = new Team("team2");

        teamRepository.save(team1);
        teamRepository.save(team2);

        // when
        Team findTeam1 = teamRepository.findById(team1.getId()).get();
        Team findTeam2 = teamRepository.findById(team2.getId()).get();

        // then
        assertThat(findTeam1).isEqualTo(team1);
        assertThat(findTeam2).isEqualTo(team2);
    }


    @Test
    @DisplayName("Team 리스트 조회를 할 수 있다.")
    void delete() {
        // given
        Team team1 = new Team("team1");
        Team team2 = new Team("team2");

        teamRepository.save(team1);
        teamRepository.save(team2);

        // when
        List<Team> teams = teamRepository.findAll();
        long count = teamRepository.count();

        // then
        assertThat(teams).hasSize(2);
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Team을 삭제를 할 수 있다.")
    void findAll() {
        // given
        Team team1 = new Team("team1");
        Team team2 = new Team("team2");

        teamRepository.save(team1);
        teamRepository.save(team2);

        // when
        teamRepository.delete(team1);
        teamRepository.delete(team2);

        List<Team> teams = teamRepository.findAll();
        long afterDeleteCount = teamRepository.count();

        // then
        assertThat(teams).isEmpty();
        assertThat(afterDeleteCount).isZero();
    }
}