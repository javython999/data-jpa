package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("Member를 저장 할 수 있다.")
    void save() {
        // given
        Member member = new Member("memberA");

        // when
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }


    @Test
    @DisplayName("Member를 단건 조회를 할 수 있다.")
    void findById() {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        // then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);
    }


    @Test
    @DisplayName("Member 리스트 조회를 할 수 있다.")
    void delete() {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findAll();
        long count = memberRepository.count();

        // then
        assertThat(members).hasSize(2);
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Member를 삭제를 할 수 있다.")
    void findAll() {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        List<Member> members = memberRepository.findAll();
        long afterDeleteCount = memberRepository.count();

        // then
        assertThat(members).isEmpty();
        assertThat(afterDeleteCount).isZero();
    }


    @Test
    @DisplayName("Member의 이름과 나이로 조회를 할 수 있다.")
    void findByUsernameAndAgeGreaterThan() {
        // given
        Member member1 = new Member("AAA", 10, null);
        Member member2 = new Member("AAA", 20, null);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);

    }

    @Test
    @DisplayName("Member의 이름으로 조회를 할 수 있다.")
    void nameQuery() {
        // given
        Member member1 = new Member("AAA", 10, null);
        Member member2 = new Member("BBB", 20, null);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findByUsername("AAA");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(10);
    }
    
    @Test
    @DisplayName("이름과 나이가 같은 Member를 조회 할 수 있다")
    void repositoryMethodQuery() {
        // given
        Member member1 = new Member("AAA", 10, null);
        Member member2 = new Member("BBB", 20, null);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findUser("AAA", 10);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(10);
    }

    @Test
    @DisplayName("Member의 username을 조회할 수 있다.")
    void findUsernameList() {
        // given
        Member member1 = new Member("AAA", 10, null);
        Member member2 = new Member("BBB", 20, null);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<String> result = memberRepository.findUsernameList();

        // then
        assertThat(result).isEqualTo(List.of("AAA", "BBB"));
    }

    @Test
    @DisplayName("MemberDto를 조회 할 수 있다.")
    void findMemberDto() {
        // given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member = new Member("AAA", 10, null);
        member.setTeam(team);

        memberRepository.save(member);

        // when
        List<MemberDto> result = memberRepository.findMemberDto();

        // then
        assertThat(result.get(0).getId()).isEqualTo(member.getId());
        assertThat(result.get(0).getUsername()).isEqualTo(member.getUsername());
        assertThat(result.get(0).getTeamName()).isEqualTo(member.getTeam().getName());
    }


    @Test
    @DisplayName("username을 List로 조건을 주고 조회 할 수 있다.")
    void findByNames() {
        // given
        Member member1 = new Member("AAA", 10, null);
        Member member2 = new Member("BBB", 20, null);
        Member member3 = new Member("CCC", 30, null);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        // when
        List<Member> result = memberRepository.findByNames(List.of("AAA", "BBB"));

        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(List.of(member1, member2));
    }


    @Test
    @DisplayName("Member List를 페이징 처리를 해서 조회할 수 있다.")
    void findByPage() {
        // given
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member2", 10, null));
        memberRepository.save(new Member("member3", 10, null));
        memberRepository.save(new Member("member4", 10, null));
        memberRepository.save(new Member("member5", 10, null));
        memberRepository.save(new Member("member6", 11, null));
        memberRepository.save(new Member("member7", 11, null));
        memberRepository.save(new Member("member8", 11, null));
        memberRepository.save(new Member("member9", 11, null));
        memberRepository.save(new Member("member10", 11, null));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(10, pageRequest);
        List<Member> content = page.getContent();
        long totalCount =  page.getTotalElements();

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        assertThat(content).hasSize(3);
        assertThat(totalCount).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    @DisplayName("Member List를 slice페이징 처리를 해서 조회할 수 있다.")
    void findBySlicePage() {
        // given
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member2", 10, null));
        memberRepository.save(new Member("member3", 10, null));
        memberRepository.save(new Member("member4", 10, null));
        memberRepository.save(new Member("member5", 10, null));
        memberRepository.save(new Member("member6", 11, null));
        memberRepository.save(new Member("member7", 11, null));
        memberRepository.save(new Member("member8", 11, null));
        memberRepository.save(new Member("member9", 11, null));
        memberRepository.save(new Member("member10", 11, null));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Slice<Member> page = memberRepository.findSliceByAge(10, pageRequest);
        List<Member> content = page.getContent();

        // then
        assertThat(content).hasSize(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }


    @Test
    @DisplayName("age가 특정 값 이상인 Member들의 age를 + 1로 수정할 수 있다.")
    void bulkAgePlust() {
        // given
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member2", 19, null));
        memberRepository.save(new Member("member3", 20, null));
        memberRepository.save(new Member("member4", 21, null));
        memberRepository.save(new Member("member5", 40, null));

        // when
        int reusltCount = memberRepository.bulkAgePlus(20);
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);

        // then
        assertThat(reusltCount).isEqualTo(3);
        assertThat(member5.getAge()).isEqualTo(41);
    }

    @Test
    @DisplayName("Member의 팀을 지연로딩 한다.")
    void findMemberByLazy() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        // when
        //List<Member> members = memberRepository.findAll();
        //List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> members = memberRepository.findEntityGraph();

        for (Member member : members) {
            System.out.println("member.getUsername = " + member.getUsername());
            System.out.println("member.getTeam = " + member.getTeam().getName());
            System.out.println("member.getTeam.getClass = " + member.getTeam().getClass());
        }
    }

    @Test
    @DisplayName("queryHint")
    void queryHint() {
        // given
        Member member = new Member("member1", 10, null);
        memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername(member.getUsername());
        findMember.setUsername("member!");

        em.flush();
        // then
    }

    @Test
    @DisplayName("lock")
    void lock() {
        // given
        Member member = new Member("member1", 10, null);
        memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername(member.getUsername());

        // then
    }

    @Test
    @DisplayName("callCustom")
    void callCustom() {
        // given
        Member member = new Member("member1", 10, null);
        memberRepository.save(member);

        // when
        List<Member> memberCustom = memberRepository.findMemberCustom();

        // then
        assertThat(memberCustom).hasSize(1);
    }

    @Test
    @DisplayName("specBasic")
    void specBasic() {
        // given
        Team team = new Team("teamA");
        em.persist(team);

        Member member1 = new Member("user1", 10, team);
        Member member2 = new Member("user2", 20, team);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        Specification<Member> spec = MemberSpe.username("user1").and(MemberSpe.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(member1.getId());
    }

    @Test
    @DisplayName("queryByExample")
    void queryByExample() {
        // given
        Team team = new Team("teamA");
        em.persist(team);

        Member member1 = new Member("user1", 10, team);
        Member member2 = new Member("user2", 20, team);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        // Probe
        Member memberFindParam = new Member("user1");
        Team teamFindParam = new Team("teamA");
        memberFindParam.setTeam(teamFindParam);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");
        Example<Member> example = Example.of(memberFindParam, matcher);

        List<Member> result = memberRepository.findAll(example);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(member1.getId());
    }

    @Test
    @DisplayName("projections")
    void projections() {
        // given
        Team team = new Team("teamA");
        em.persist(team);

        Member member1 = new Member("member1", 10, team);
        Member member2 = new Member("member2", 20, team);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("member1");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo(member1.getUsername());
    }


    @Test
    @DisplayName("projectionsDto")
    void projectionsDto() {
        // given
        Team team = new Team("teamA");
        em.persist(team);

        Member member1 = new Member("member1", 10, team);
        Member member2 = new Member("member2", 20, team);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        List<UsernameOnlyDto> result = memberRepository.findDtoByUsername("member1");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo(member1.getUsername());
    }

}