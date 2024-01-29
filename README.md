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