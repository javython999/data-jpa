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