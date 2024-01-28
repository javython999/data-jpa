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
class TeamJpaRepositoryTest {

    @Autowired
    TeamJpaRepository teamJpaRepository;

    @Test
    @DisplayName("Team을 저장 할 수 있다.")
    void save() {
        // given
        Team team = new Team("TeamA");

        // when
        Team savedTeam = teamJpaRepository.save(team);
        Team findTeam = teamJpaRepository.find(savedTeam.getId());

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

        teamJpaRepository.save(team1);
        teamJpaRepository.save(team2);

        // when
        Team findTeam1 = teamJpaRepository.findById(team1.getId()).get();
        Team findTeam2 = teamJpaRepository.findById(team2.getId()).get();

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

        teamJpaRepository.save(team1);
        teamJpaRepository.save(team2);

        // when
        List<Team> teams = teamJpaRepository.findAll();
        long count = teamJpaRepository.count();

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

        teamJpaRepository.save(team1);
        teamJpaRepository.save(team2);

        // when
        teamJpaRepository.delete(team1);
        teamJpaRepository.delete(team2);

        List<Team> teams = teamJpaRepository.findAll();
        long afterDeleteCount = teamJpaRepository.count();

        // then
        assertThat(teams).isEmpty();
        assertThat(afterDeleteCount).isZero();
    }
}