# 프로젝트 설명

<aside>
📢

개인화된 맛집 추천을 위한 SNS | 목표: 목표 : JAVA / SPRING 학습

</aside>

# 기술 스택

- Language: `JAVA`
- Framework: `SpringBoot`
- Database: `MySQL`, `Redis`, `ElastiSearch`
- ORM :  `SpringDataJPA`

# 주요 구현 내용

- Spring-security와 jwt를 활용한 토큰 기반 인증
- 전략 패턴을 활용한 소셜 회원가입/로그인
- mySQL, JPA를 활용하여 데이터 관리
- AOP를 활용한 정적 자원 권한 관리
- Redis를 활용한 좋아요
- Redis Pub/Sub 활용한 피드 임시 저장
- ElasticSearch를 활용한 가게 검색, 위치 검색 시스템
- mySQL 전문 검색을 활용한 유저 검색 시스템
- 피드, 댓글, 대댓글 API
- Spring-scheduler, CompleteFuture를 활용하여 서울시 음식점 공공 API 데이터 업데이트 시스템
    - 동시성 충돌 문제 해결을 위해 낙관적 Lock과 retry 로직

# 구현 기능

### 계정 관리

<aside>
🪪

기능

- **회원 가입**: **`자체 회원 가입`**혹은 **`구글`**을 통해 회원 가입
- **로그인**: **`이메일`**혹은 **`소셜(구글)`** 로그인
</aside>

### 피드 관리

<aside>
📄

기능

- **피드 리스트 조회**: 최근 7일 이내의 추천 피드를 조회
    - Candiate → Evalaution → filter 구조에서 차용
    - 후보군
        - (Innbound) 팔로우 한 유저의 글의 최신 글
        - (Innbound)내가 최근 본 피드의 유저글
        - (Innbound)내가 최근 본 피드의 가게 카테고리
    - 평가 함수
        - 현재는 시간 순
        - 평가 점수를 Redis Cahche Zset에 저장
    - filter
        - feed의 Configuration을 검토 (예정)
- **상세 피드 조회**:  피드의 상세 내용을 조회
    - Redis에 나의 검색 리스트에 User와 Restaurant Category Zset으로 저장 ( 시간 + 회수 ) → 피드 추천에 활용
- **피드 작성**: 피드를 작성하거나 수정
- **피드 임시저장**:
    - 피드 내용을 작성할 때, 임시 저장
    - 임시 저장 된 내용 조회
- **피드 사진**:  해당 피드의 사진을 조회 및 저장
</aside>

### 좋아요

<aside>
💟

기능

- **좋아요**: 피드에 대한 좋아요
</aside>

### 댓글 쓰기

<aside>
🗒️

기능

- **댓글**: 댓글을 조회, 작성, 수정, 삭제 할 수 있습니다.
- **대댓글**: 대댓글을 조회, 작성, 수정, 삭제 할 수 있습니다.
</aside>

### 가게 관리

<aside>
🏪

기능

- **가게 데이터 저장**:공공 데이터를 활용해서 서울시 가게 정보 업데이트
- **가게 정보 조회**: 가게 상세 정보 조회
</aside>

### 프로필

<aside>
👬

기능

- **유저 정보**: 이름, 닉네임, 게시글 수, 팔로잉/팔로워 수 조회
- **게시글**: 해당 유저가 작성한 게시글 조회
- **팔로우**: 해당 유저를 팔로우/언팔로우
</aside>

### 검색

<aside>
🔎

기능

- **User 검색**: MySQL 전문검색을 활용하여 유저 검색
- **가게 반경 검색**: ElastiSearch를 활용하여 특정 좌표 반경 검색
- **가게 검색**: 댓글을 조회, 작성, 수정, 삭제 할 수 있습니다.
</aside>
