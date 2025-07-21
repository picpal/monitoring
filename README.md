# Monitoring Framework

Spring Boot 기반의 모니터링 프레임워크 프로젝트입니다.

## 📋 프로젝트 개요

이 프로젝트는 Spring Boot 3.2.2를 기반으로 한 모니터링 시스템의 보일러플레이트입니다. 다음과 같은 주요 기능들을 포함하고 있습니다:

- **Spring Boot 3.2.2** - 최신 Spring Boot 프레임워크
- **Spring Security** - 보안 기능
- **MyBatis 3.0.3** - 데이터베이스 ORM (Spring Boot 3.2.2 호환)
- **H2 Database** - 개발용 인메모리 데이터베이스
- **Jasypt** - 설정 파일 암호화
- **Swagger/OpenAPI** - API 문서화
- **Actuator** - 모니터링 및 관리
- **Quartz** - 스케줄링
- **Apache POI** - Excel 파일 처리
- **Thymeleaf** - 서버 사이드 템플릿 엔진

## 🚀 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.2.2**
- **Spring Security**
- **Spring Data JPA**
- **MyBatis 3.0.3** (Spring Boot 3.2.2 호환)
- **H2 Database**
- **Jasypt 3.0.4**
- **Quartz Scheduler**
- **Apache POI**
- **Thymeleaf**

### Build Tool
- **Gradle 8.x**

### Documentation
- **Swagger/OpenAPI 3.0**
- **Spring REST Docs**

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── com/picpal/framework/
│   │       ├── BoilerplateApplication.java
│   │       ├── common/
│   │       │   ├── config/
│   │       │   │   ├── JasyptConfig.java
│   │       │   │   └── WebSecurityConfiguration.java
│   │       │   ├── constant/
│   │       │   ├── enums/
│   │       │   ├── exception/
│   │       │   └── utils/
│   │       ├── monitoring/
│   │       │   ├── controller/
│   │       │   │   └── MonitoringViewController.java
│   │       │   ├── repository/
│   │       │   │   ├── model/
│   │       │   │   │   └── MonitoringResult.java
│   │       │   │   └── MonitoringResultRepository.java
│   │       │   └── vo/
│   │       │       └── TransactionAnalysisVO.java
│   │       └── sample/
│   │           ├── controller/
│   │           ├── dto/
│   │           ├── mapper/
│   │           ├── repository/
│   │           ├── service/
│   │           └── vo/
│   └── resources/
│       ├── application.yml
│       ├── application-local.yml
│       ├── mappers/
│   │   └── sampleMapper_sql.xml
│       ├── templates/
│   │   └── monitoring-report.html
│       └── static/
└── test/
    └── java/
        └── com/picpal/framework/
```

## ⚙️ 설정

### Spring Boot 3.2.2 호환성

프로젝트는 Spring Boot 3.2.2와 호환되는 의존성들을 사용합니다:

#### 주요 의존성 버전
- **MyBatis Spring Boot Starter**: 3.0.3 (Spring Boot 3.2.2 호환)
- **Jasypt Spring Boot Starter**: 3.0.4
- **SpringDoc OpenAPI**: 2.0.2
- **JSoup**: 1.17.2 (최신 버전으로 업데이트)

#### 해결된 호환성 문제
1. **MyBatis 3.0.2 → 3.0.3**: `Invalid value type for attribute 'factoryBeanObjectType'` 오류 해결
2. **XML Mapper DOCTYPE**: MyBatis XML 매퍼 파일에 올바른 DOCTYPE 선언 추가
3. **JPA 쿼리 호환성**: H2 데이터베이스와 호환되는 JPA 쿼리로 수정

### Jasypt 암호화 설정

프로젝트는 Jasypt를 사용하여 민감한 정보를 암호화합니다.

#### JVM 옵션 설정
```bash
# Windows
-Djasypt.enc.pre=0000 -Djasypt.enc.post=1111

# Linux/Mac
-Djasypt.enc.pre=0000 -Djasypt.enc.post=1111
```

#### 암호화된 값 생성
JasyptConfigTest.java를 실행하여 암호화된 값을 생성할 수 있습니다:

```bash
# 특정 테스트 실행
./gradlew test --tests "*JasyptConfigTest*"
```

또는 IDE에서 `JasyptConfigTest.java` 파일의 `stringEncryptor()` 메서드를 직접 실행할 수 있습니다.

#### 암호화된 값 사용
설정 파일에서 `ENC()` 태그로 감싸진 값들은 암호화된 값입니다:

```yaml
spring:
  datasource:
    username: ENC(v+KejgQ9O2W2SErT135QUUpJKl5xl+ZatNpN/IO3iXg+jjQxJHqv1Yn53x8fv+ja)
    password: ENC(U4pRcwReVADOeC2aHvgvO0fp35SCIuBJEtRqSMhaiqsk9cnfm6KjMlLt0TSyEX4B)
```

**참고**: JasyptConfigTest.java에서 사용하는 암호화 키는 `00001111` (pre: 0000 + post: 1111)입니다.

### Spring Security 설정

인증 없이 접근 가능한 경로들을 변수로 관리하여 유지보수성을 향상시켰습니다:

```java
private static final String[] PERMIT_ALL_PATHS = {
    "/monitoring/**",  // 모니터링 페이지
    "/actuator/**",    // Spring Boot Actuator
    "/swagger-ui/**",  // Swagger UI
    "/v3/api-docs/**" // OpenAPI 문서
};
```

## 🛠️ 실행 방법

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd monitoring
```

### 2. JVM 옵션 설정
IDE에서 실행할 때 JVM 옵션을 설정하거나, 명령줄에서 다음과 같이 실행:

```bash
# Gradle로 실행
./gradlew bootRun --args="-Djasypt.enc.pre=0000 -Djasypt.enc.post=1111"

# 또는 JAR 파일로 실행
java -Djasypt.enc.pre=0000 -Djasypt.enc.post=1111 -jar build/libs/monitoring-0.0.1-SNAPSHOT.jar
```

### 3. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 4. 빌드
```bash
./gradlew build
```

## 🌐 접속 정보

애플리케이션 실행 후 다음 URL들로 접속할 수 있습니다:

- **메인 애플리케이션**: http://localhost:8080
- **모니터링 리포트**: http://localhost:8080/monitoring/report
- **H2 Console**: http://localhost:8080/h2-console
- **Swagger UI**: http://localhost:8080/docs/swagger
- **API 문서**: http://localhost:8080/docs/open-api-3.0.1.json
- **Actuator Health**: http://localhost:8080/actuator/health
- **Actuator Info**: http://localhost:8080/actuator/info

### H2 Database 접속 정보
- **JDBC URL**: `jdbc:h2:./monitoring`
- **Username**: `ADMIN`
- **Password**: `password`

## 📚 API 문서

### 모니터링 API
- **GET** `/monitoring/report` - 모니터링 리포트 뷰 (Thymeleaf 템플릿)

### 샘플 API
- **GET** `/api/v1/sample` - 샘플 엔드포인트

### API 문서화
- Swagger UI를 통해 실시간 API 문서를 확인할 수 있습니다
- REST Docs를 통한 API 문서 자동 생성

#### REST Docs 생성
REST Docs는 테스트 실행 시 자동으로 생성됩니다:

```bash
# REST Docs 생성 (테스트 실행)
./gradlew test

# REST Docs 문서 생성
./gradlew asciidoctor
```

생성된 문서는 다음 위치에서 확인할 수 있습니다:
- **REST Docs 스니펫**: `build/generated-snippets/`
- **최종 문서**: `src/main/resources/static/docs/`

## 🔧 개발 환경 설정

### 필수 요구사항
- Java 17 이상
- Gradle 8.x
- IDE (IntelliJ IDEA, Eclipse, VS Code 등)

### IDE 설정
1. 프로젝트를 IDE에서 열기
2. Gradle 프로젝트로 인식되도록 설정
3. JVM 옵션 설정:
   - `-Djasypt.enc.pre=0000`
   - `-Djasypt.enc.post=1111`

## 📝 주요 기능

### 1. 보안
- Spring Security를 통한 인증/인가
- Jasypt를 통한 설정 파일 암호화
- 인증 없이 접근 가능한 경로 관리

### 2. 데이터베이스
- H2 인메모리 데이터베이스 (개발용)
- MyBatis를 통한 데이터 접근
- JPA 지원

### 3. 모니터링
- Spring Boot Actuator
- 로깅 시스템
- 헬스 체크 엔드포인트

### 4. 스케줄링
- Quartz를 통한 작업 스케줄링

### 5. 문서화
- Swagger/OpenAPI 3.0
- Spring REST Docs

### 6. 웹 뷰
- Thymeleaf 템플릿 엔진
- 모니터링 리포트 뷰

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests "*JasyptConfigTest*"
```

### Jasypt 암호화 테스트
JasyptConfigTest는 설정 파일의 민감한 정보를 암호화하는 데 사용됩니다:

- **위치**: `src/test/java/com/picpal/framework/common/config/JasyptConfigTest.java`
- **기능**: 사용자명과 비밀번호를 ENC() 형태로 암호화
- **암호화 키**: `00001111` (JVM 옵션의 pre + post 값)
- **사용법**: 테스트 실행 후 로그에서 암호화된 값을 확인하여 application.yml에 적용

### REST Docs 테스트
API 문서 자동 생성을 위한 REST Docs 테스트가 포함되어 있습니다:

- **위치**: `src/test/java/com/picpal/framework/sample/controller/SampleControllerTest.java`
- **기능**: API 엔드포인트 테스트 및 문서 자동 생성
- **설정**: `@AutoConfigureRestDocs` 어노테이션으로 REST Docs 자동 설정
- **사용법**: 테스트 실행 시 API 문서 스니펫이 자동 생성됨

## 🔄 최근 업데이트

### Spring Boot 3.2.2 호환성 개선
- MyBatis Spring Boot Starter 3.0.3으로 업그레이드
- JSoup 1.17.2로 업데이트
- JUnit Jupiter 의존성 정리
- javax.servlet-api 의존성 제거 (Spring Boot 3.x는 Jakarta EE 사용)

### MyBatis 설정 개선
- XML 매퍼 파일에 올바른 DOCTYPE 선언 추가
- H2 데이터베이스와 호환되는 JPA 쿼리로 수정

### Spring Security 설정 개선
- 인증 없이 접근 가능한 경로들을 변수로 관리
- 모니터링 페이지 접근 허용

### Thymeleaf 템플릿 추가
- 모니터링 리포트 뷰 템플릿 추가
- CSS와 HTML 분리

## 📦 배포

### JAR 파일 생성
```bash
./gradlew bootJar
```

### 실행
```bash
java -Djasypt.enc.pre=0000 -Djasypt.enc.post=1111 -jar build/libs/monitoring-0.0.1-SNAPSHOT.jar
``` 

## 📝 로그 표준화 및 트래킹ID(MDC) 활용

### 로그 포맷 표준화 (logback.xml)

```xml
<pattern>
    [%X{requestId}] [%-5level] [%thread] [%date{yyyy-MM-dd HH:mm:ss}] %logger{96} [%line] - %msg%n
</pattern>
```
- 모든 로그에 requestId(트래킹ID)가 포함되어 장애 추적이 용이합니다.

### MDC(requestId) 자동 주입

`com.picpal.framework.common.config.MDCFilter`에서 모든 요청마다 UUID 기반 requestId를 MDC에 주입합니다.

```java
@Component
public class MDCFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
        }
    }
}
```

### VOC 대응 및 로그 추적 방법

- 장애/오류 발생 시, 로그에서 `[requestId]`로 검색하면 해당 요청의 전체 흐름을 추적할 수 있습니다.
- 에러 응답에도 requestId(트래킹ID)를 포함하면, 고객 문의 시 빠른 추적이 가능합니다.
- 로그 집계 시스템(ELK, Sentry 등)과 연동 시, requestId로 장애 패턴 분석 및 알람 설정이 가능합니다.
- 예외 발생 시 로그 예시:

```
[1a2b3c4d-...] [ERROR] [http-nio-8080-exec-1] [2024-05-01 12:34:56] com.picpal.framework.monitoring.service.impl.MonitoringServiceImpl [123] - 모니터링 실행 중 오류 발생
com.picpal.framework.monitoring.exception.MonitoringException: 모니터링 실행 중 오류 발생
    at ...
```

- 운영자는 requestId, 에러 코드, 메시지, stack trace를 종합해 장애 원인과 위치를 빠르게 파악할 수 있습니다. 