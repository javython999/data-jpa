# 공통 인터페이스 설정
***

### JavaConfig 설정(스프링부트 사용시 생략 가능)
```java
@Configuration
@EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
public AppConfig() {}
```
* 스프링 부트 사용시 `@SpringBootApplication` 위치를 지정(해당 패키지 하위 패키지 인식)
* 만약 위치가 달라지면 `@EnableJpaRepositories` 필요

### 스프링 데이터 JPA가 구현 클래스 대신 생성
* `org.springframework.data.jpa.repository.JpaRepository` 상속한 인터페이스는 스프링 데이터 JPA가 `proxy`객체를 생성한다.
* `@Repository` 애노테이션 생략가능
  * 컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리 하기 때문에 생략가능
  * JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리한다.
***
# Query Method
***
### 메소드 이름으로 쿼리 생성
* JPA 
```java
public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
    return em.createQuery("select m from Member m where m.username = :username and m.age >:age")
            .setParameter("username", username)
            .setParameter("age", age)
            .getResultList();
}
```
* 스프링 데이터 JPA
```java
public interface MemberRepository extends JpaRepository<Member, Long> { 
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
}
```
스프링 데이터 JPA는 메소드의 이름을 분석해 JPQL을 생성하고 실행한다.
### JPA Named Qeury
* @NamedQuery 애노테이션으로 NamedQuery 정의
```java
@Entity
@NamedQuery(
        name="Member.findByUsername", 
        query="select m from Member m where m.username = :username")
public class Member {
  ...
}
```
* JPA 
```java
public class MemberRepository { 
    public List<Member> findByUsername(String username) {
        ...
        List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }
} 
```
* 스프링 데이터 JPA
```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsername(@Param("username") String username);
}
```

### Entity가 아닌 객체로 반환
* 단순히 하나의 값을 조회
```java
@Query("select m.username from Member m")
List<String> findUsernameList();
```
메소드의 반환 타입을 지정해 하나의 값을 리스트로 조회 할 수 있다.

* 여러 값을 DTO로 조회
```java
@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) " +
 "from Member m join m.team t")
List<MemberDto> findMemberDto();
```
1. DTO 객체를 만들고 `parameter가 필요한 생성자`를 만든다.
2. select절에 new명령어와 DTO의 패키지 경로를 적는다.
3. 생성자에 parameter를 넘겨주듯이 select절을 작성한다.

### 파라미터 바인딩
* 위치 기반
```java
@Query("select m from Member m where m.id = ?0")
Member findById(Long id);
```
* 이름 기반
```java
@Query("select m from Member m where m.id = :id")
Member findById(@Param("id")Long id);
```
> 코드의 가독성, 유지보수성을 위해 이름기반 바인딩을 사용 권장
* 컬렉션 파라미터 바인딩
```java
@Query("select m from Member m where m.username in :names")
List<Member> findByNames(@Param("names") List<String> names);
```
### 페이징과 정렬
* JPA
```java
// paging repository code
public List<Member> findByPage(int age, int offset, int limit) {
    return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
            .setParameter("age", age)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
}

public long totalCount(int age) {
    return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
          .setParameter("age", age)
          .getSingleResult();
}
```
```java
// paging test code
@Test
public void paging() throws Exception {
    //given
    memberJpaRepository.save(new Member("member1", 10));
    memberJpaRepository.save(new Member("member2", 10));
    memberJpaRepository.save(new Member("member3", 10));
    memberJpaRepository.save(new Member("member4", 10));
    memberJpaRepository.save(new Member("member5", 10));
    
    int age = 10;
    int offset = 0;
    int limit = 3;
    
    //when
    List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
    long totalCount = memberJpaRepository.totalCount(age);
    
    //then
    assertThat(members.size()).isEqualTo(3);
    assertThat(totalCount).isEqualTo(5);
}
```
* 스프링 데이터 JPA
> 페이징과 정렬 파라미터
> * 페이징: `org.springframework.data.domain.Pageable`
> * 정렬 : `org.springframework.data.domain.Sort`

> 페이징 반환 타입
> * total count 쿼리 결과를 포함하는 페이징 : `org.springframework.data.domain.Page`
> * total count 없이 다음 페이지 존재만 확인 하는 페이징 : `org.springframework.data.domain.Slice`

```java
//페이징 조건과 정렬 조건 설정
@Test
public void page() throws Exception {
    //given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
    
    //when
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
    Page<Member> page = memberRepository.findByAge(10, pageRequest);

    //then
    List<Member> content = page.getContent(); //조회된 데이터
    assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
    assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
    assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
    assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
    assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
    assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
}
```
* 두 번째 파라미터로 받은 `Pageable`은 인터페이스다. 따라서 실제 사용할 때는 해당 인터페이스를 구현한
  `org.springframework.data.domain.PageRequest` 객체를 사용한다.
* `PageRequest` 생성자의 첫 번째 파라미터에는 현재 페이지를, 두 번째 파라미터에는 조회할 데이터 수를 입력
  한다. 여기에 추가로 정렬 정보도 파라미터로 사용할 수 있다. 참고로 페이지는 `0`부터 시작한다.

> count 쿼리를 분리해 성능최적화를 할 수 있다.
> 
> ```java
> @Query(
>   value = "select m from Member m left join m.team t", 
>   countQuery = "select count(m.username) from Member m"
> )
> Page<Member> findMemberAllCountBy(Pageable pageable);
> ```

* 페이지를 유지하면서 ``entity``를 ``dto``로 변환
```java
Page<Member> page = memberRepository.findByAge(10, pageRequest);
Page<MemberDto> dtoPage = page.map(m -> new MemberDto());
```
### 벌크 수정 쿼리
* JPA
```java
public int bulkAgePlus(int age) {
    int resultCount = em.createQuery("update Member m set m.age = m.age + 1" + "where m.age >= :age")
            .setParameter("age", age)
            .executeUpdate();
    return resultCount;
 }


@Test
public void bulkUpdate() throws Exception {
    //given
    memberJpaRepository.save(new Member("member1", 10));
    memberJpaRepository.save(new Member("member2", 19));
    memberJpaRepository.save(new Member("member3", 20));
    memberJpaRepository.save(new Member("member4", 21));
    memberJpaRepository.save(new Member("member5", 40));
    
    //when
    int resultCount = memberJpaRepository.bulkAgePlus(20);
  
    //then
    assertThat(resultCount).isEqualTo(3);
}
```
* 스프링 데이터 JPA
```java
@Modifying
@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
int bulkAgePlus(@Param("age") int age);


@Test
public void bulkUpdate() throws Exception {
    //given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 19));
    memberRepository.save(new Member("member3", 20));
    memberRepository.save(new Member("member4", 21));
    memberRepository.save(new Member("member5", 40));
  
    //when
    int resultCount = memberRepository.bulkAgePlus(20);
    
    //then
    assertThat(resultCount).isEqualTo(3);
}
```
* 벌크성 수정, 삭제 쿼리는 `@Modifying` 애노테이션을 적용
  * 사용하지 않으면 예외 발생(`org.hibernate.hql.internal.QueryExecutionRequestException: Not supported
      for DML operations`)
* 벌크성 쿼리를 실행하고나서 영속성 컨텍스트를 초기화: `@Modifying(clearAutomatically = true)` / 기본값 `false`
  * 이 옵션없이 조회시 DB에서 데이터를 가져온 후 같은 entity가 영속성 컨텍스트에 존재하는 것을 확인하고 DB에서 가져온 데이터는 버리고 영속성 컨텍스트의 entity를 반환하게 된다.

> 1. 영속성 컨텍스트에 entity가 없는 상태에서 벌크 연산을 수행한다.
> 2. 부득이하게 영속성 컨텍스트에 entity가 있는 경우 벌크연산 수행 직후 영속성 컨텍스트를 초기화 한다.

### @EntityGraph
연관된 entity들을 SQL 한번에 조회
> member -> team은 지연로딩 관계이다. 따라서 다음과 같이 team의 데이터를 조회할 때 마다 쿼리가 실행된다.(N+1문제)
```java
//Hibernate 기능으로 확인
Hibernate.isInitialized(member.getTeam())

//JPA 표준 방법으로 확인
PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
util.isLoaded(member.getTeam());
```

N+1문제를 해결 하려면 fetch join이 필요하다.
* JPQL fetch join
```java
@Query("select m from Member m left join fetch m.team")
List<Member> findMemberFetchJoin();
```

* 스프링 데이터 JPA 
> JPA가 제공하는 엔티티 그래프 기능을 편리하게 사용하게 도와준다. 이 기능을 사용하면 JPQL 없이 페치 조인을 사용할 수 있다. (JPQL + 엔티티 그래프도 가능)
```java
//공통 메서드 오버라이드
@Override
@EntityGraph(attributePaths = {"team"})
List<Member> findAll();

//JPQL + 엔티티 그래프
@EntityGraph(attributePaths = {"team"})
@Query("select m from Member m")
List<Member> findMemberEntityGraph();

//메서드 이름으로 쿼리에서 특히 편리하다.
@EntityGraph(attributePaths = {"team"})
List<Member> findByUsername(String username);
```