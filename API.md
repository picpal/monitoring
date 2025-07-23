# Monitoring System API 명세서

## 개요

모니터링 시스템은 거래 데이터를 분석하여 이상 패턴을 감지하고, 결과를 Redmine 이슈로 등록할 수 있는 시스템입니다.

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **서버 포트**: 8080

## 목차

1. [Monitoring API](#monitoring-api)
2. [Redmine API](#redmine-api)
3. [View Pages](#view-pages)
4. [Data Models](#data-models)
5. [Error Codes](#error-codes)

---

## Monitoring API

모니터링 실행 및 결과 조회를 위한 API입니다.

### 1. 모니터링 실행

지정된 분석 기간으로 모니터링을 실행합니다.

**요청**
```http
POST /api/monitoring/execute
```

**파라미터**

| 이름 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| analysisPeriod | String | O | 분석 기간 | `09:00`, `13:00`, `18:00` |

**요청 예시**
```bash
curl -X POST "http://localhost:8080/api/monitoring/execute?analysisPeriod=09:00" \
  -H "Content-Type: application/json"
```

**응답**
```json
{
  "id": 1,
  "monitoringDate": "2024-01-15T09:00:00",
  "analysisPeriod": "09:00",
  "totalTransactions": 1250,
  "totalAmount": 125000000,
  "abnormalCount": 15,
  "htmlContent": "<html>...</html>",
  "redmineIssueId": "ISSUE-123",
  "status": "COMPLETED",
  "errorMessage": null,
  "createdAt": "2024-01-15T09:05:00",
  "updatedAt": "2024-01-15T09:05:00"
}
```

### 2. 특정 기간 모니터링 실행

지정된 시작/종료 시간과 분석 기간으로 모니터링을 실행합니다.

**요청**
```http
POST /api/monitoring/execute/period
```

**파라미터**
| 이름 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| startDate | LocalDateTime | O | 시작 시간 (ISO DateTime) | `2024-01-15T09:00:00` |
| endDate | LocalDateTime | O | 종료 시간 (ISO DateTime) | `2024-01-15T18:00:00` |
| analysisPeriod | String | O | 분석 기간 | `09:00` |

**요청 예시**
```bash
curl -X POST "http://localhost:8080/api/monitoring/execute/period?startDate=2024-01-15T09:00:00&endDate=2024-01-15T18:00:00&analysisPeriod=09:00" \
  -H "Content-Type: application/json"
```

**응답**
```json
{
  "id": 2,
  "monitoringDate": "2024-01-15T09:00:00",
  "analysisPeriod": "09:00",
  "totalTransactions": 2500,
  "totalAmount": 250000000,
  "abnormalCount": 28,
  "htmlContent": "<html>...</html>",
  "redmineIssueId": "ISSUE-124",
  "status": "COMPLETED",
  "errorMessage": null,
  "createdAt": "2024-01-15T09:10:00",
  "updatedAt": "2024-01-15T09:10:00"
}
```

### 3. 모니터링 상태 확인

사용 가능한 분석 기간과 마지막 실행 시간을 확인합니다.

**요청**
```http
GET /api/monitoring/status
```

**요청 예시**
```bash
curl -X GET "http://localhost:8080/api/monitoring/status" \
  -H "Content-Type: application/json"
```

**응답**
```json
{
  "availableAnalysisPeriods": ["09:00", "13:00", "18:00"],
  "lastExecution": "2024-01-15T09:05:00"
}
```

---

## Redmine API

Redmine 이슈 관리를 위한 API입니다.

### 1. Redmine 이슈 등록

Redmine에 이슈를 등록합니다.

**요청**
```http
POST /api/redmine/issue/{projectKey}
```

**경로 파라미터**
| 이름 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| projectKey | String | O | Redmine 프로젝트 키 | `MONITORING` |

**요청 본문**
```json
{
  "subject": "모니터링 이상 감지 - 거래량 급증",
  "description": "오늘 09:00 분석 결과 거래량이 평균 대비 150% 증가했습니다.",
  "projectId": 1,
  "trackerId": 1,
  "priorityId": 3,
  "customFields": "{}",
  "notes": "자동 생성된 이슈입니다."
}
```

**요청 예시**
```bash
curl -X POST "http://localhost:8080/api/redmine/issue/MONITORING" \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "모니터링 이상 감지 - 거래량 급증",
    "description": "오늘 09:00 분석 결과 거래량이 평균 대비 150% 증가했습니다.",
    "projectId": 1,
    "trackerId": 1,
    "priorityId": 3,
    "customFields": "{}",
    "notes": "자동 생성된 이슈입니다."
  }'
```

**응답**
```json
{
  "issueId": 12345
}
```

---

## View Pages

웹 페이지 뷰 엔드포인트입니다.

### 1. 모니터링 리포트 뷰

Thymeleaf 템플릿을 사용한 모니터링 리포트 페이지입니다.

**요청**
```http
GET /monitoring/report
```

**요청 예시**
```bash
curl -X GET "http://localhost:8080/monitoring/report"
```

**응답**
- HTML 페이지 반환
- 샘플 거래 분석 데이터를 포함한 리포트 화면

---

## Data Models

### MonitoringResultDTO

| 필드명 | 타입 | 설명 |
|--------|------|------|
| id | Long | 모니터링 결과 ID |
| monitoringDate | LocalDateTime | 모니터링 실행 날짜 |
| analysisPeriod | String | 분석 기간 |
| totalTransactions | Integer | 총 거래 건수 |
| totalAmount | BigDecimal | 총 거래 금액 |
| abnormalCount | Integer | 이상 거래 건수 |
| htmlContent | String | HTML 리포트 내용 |
| redmineIssueId | String | Redmine 이슈 ID |
| status | MonitoringStatus | 모니터링 상태 |
| errorMessage | String | 오류 메시지 |
| createdAt | LocalDateTime | 생성 시간 |
| updatedAt | LocalDateTime | 수정 시간 |

### RedmineIssueDTO

| 필드명 | 타입 | 설명 |
|--------|------|------|
| subject | String | 이슈 제목 |
| description | String | 이슈 설명 |
| projectId | Integer | 프로젝트 ID |
| trackerId | Integer | 트래커 ID |
| priorityId | Integer | 우선순위 ID |
| customFields | String | 커스텀 필드 (JSON 문자열) |
| notes | String | 노트 |

### MonitoringStatus

| 값 | 설명 |
|----|------|
| PENDING | 대기중 |
| PROCESSING | 처리중 |
| COMPLETED | 완료 |
| FAILED | 실패 |
| ERROR | 오류 |

---

## Error Codes

### HTTP 상태 코드

| 코드 | 설명 |
|------|------|
| 200 | 성공 |
| 400 | 잘못된 요청 |
| 500 | 서버 내부 오류 |

### 오류 응답 예시

```json
{
  "timestamp": "2024-01-15T09:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "모니터링 실행 중 오류가 발생했습니다.",
  "path": "/api/monitoring/execute"
}
```

---

## 사용 예시

### 1. 기본 모니터링 실행 플로우

```bash
# 1. 모니터링 상태 확인
curl -X GET "http://localhost:8080/api/monitoring/status"

# 2. 모니터링 실행
curl -X POST "http://localhost:8080/api/monitoring/execute?analysisPeriod=09:00"

# 3. 이상 감지 시 Redmine 이슈 등록
curl -X POST "http://localhost:8080/api/redmine/issue/MONITORING" \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "모니터링 이상 감지",
    "description": "거래량 급증 감지",
    "projectId": 1,
    "trackerId": 1,
    "priorityId": 3
  }'
```

### 2. 특정 기간 모니터링

```bash
# 특정 기간 모니터링 실행
curl -X POST "http://localhost:8080/api/monitoring/execute/period?startDate=2024-01-15T09:00:00&endDate=2024-01-15T18:00:00&analysisPeriod=09:00"
```

### 3. 웹 리포트 확인

브라우저에서 `http://localhost:8080/monitoring/report` 접속

---

## 참고사항

- 모든 날짜/시간은 ISO DateTime 형식을 사용합니다.
- 분석 기간은 `09:00`, `13:00`, `18:00` 중 하나를 사용해야 합니다.
- Redmine 프로젝트 키는 시스템 설정에 따라 다를 수 있습니다.
- HTML 리포트는 Thymeleaf 템플릿을 사용하여 생성됩니다.