package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("Member를 저장 할 수 있다.")
    void save() {
        // given
        Member member = new Member("memberA");

        // when
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());

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

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

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

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        List<Member> members = memberJpaRepository.findAll();
        long count = memberJpaRepository.count();

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

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        List<Member> members = memberJpaRepository.findAll();
        long afterDeleteCount = memberJpaRepository.count();

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

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);


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

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        
        // when
        List<Member> result = memberJpaRepository.findByUsername("AAA");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(10);
        
    }
}