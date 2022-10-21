# KB 국민은행 소프트웨어 경진대회_(도사)_도사들 백엔드 소스코드

프론트엔드(React.js): https://github.com/Leekee0905/DonationChallenge  
시연영상 : https://www.youtube.com/watch?v=dLH32csWlbM

# 목차
* 백엔드 작업 내용
* 프론트엔드 작업 내용

-------

# 백엔드 작업내용 (양철진)

## 프로젝트 세팅
프로젝트에 필요한 JPA, DB 연동 작업 진행
yml 과 gradle 을 사용

## 요구사항 분석
프로젝트 시작과 동시에 주제를 정하고 사용자와 개발자 입장에서 요구사항을 분석함

## ERD 설계
요구사항 분석 후 ERD 설계 
ERD 설계를 바탕으로 JPA 를 활용하여 Domain 객체 생성

## 회원가입
회원가입 기능 개발 및 naver 이메일 서비스를 이용하여 이메일 인증 서비스 기능 구현
회원가입시 필요한 memberId 중복성 검사 실행

## 로그인 기능
세션값을 이용하여 로그인을 하도록하고 JsonIgnore 를 사용하여 비밀번호나 관계에 있는 값들을 클라이언트에 보내지 못하도록 함

## 회원탈퇴
세션을 통해 로그인이 되어있는지 확인한 후 DB에서 해당 회원정보를 지우고 즉시탈퇴되도록 함

## 마이페이지 수정 기능
* 비밀번호와 전화번호를 수정할 수 있도록 함
* 이메일 변경시 이메일 인증번호를 통해 다시 인증받도록 함
* 클라이언트가 원하는 정보를 받을 수 있도록 dto 설정
* 댓글 단 내용 및 챌린지 참여내역 기부 내역, 좋아요를 누른 챌린지의 정보 등 활동내역을 조회할 수 있도록 하는 기능 개발


## 챌린지 관련 기능

* 챌린지 생성 기능 구현
* 챌린지 생성 후 사용자가 설정한 ENDTIME 에 맞춰 해당 챌린지의 State 가 end 로 변경되도록 함 -> @Scheduled 어노테이션을 사용하여 구현
* 챌린지 수정 및 삭제 기능 완료
* 댓글달기 기능 개발 (MultipartFile 을 이용하여 동영상을 업로드 및 댓글 내용을 DB 에 저장하도록 함) -> yml 에서 따로 동영상의 maxsize 를 설정해줘야함

## 기부 기능
* Import 를 사용하여 결제시스템을 구현
* 챌린지 삭제 시 기부한 사람들에게 다시 금액을 돌려주도록 하는 결제 취소 시스템 기능 개발

## 검색기능 개발
SpringData JPA 를 사용하여 기부처와 제목을 가지고 특정 챌린지를 검색할 수 있도록 설정 @Query 어노테이션과 검색용레포지토리 interface 를 활용하여 기능 개발

* Bean Validation 과 Session , JsonIgnore 를 적극 사용

--------------

# 프론트엔드 (이기성)

## 기부 챌린지 프로젝트 (도사들)
220809 프로젝트 시작

## 회원가입 구현
220812 회원가입 연동 확인 및 bootstrap 사용해서 디자인 조금

## 챌린지 생성
220819 챌린지 업로드 폼 생성  

220822 챌린지 리스트 정보 표시 및 삭제 기능 추가

## 챌린지 수정 및 삭제

220825 챌린지 세부페이지 정보 표시, 간략한 디자인 폼 구성, 챌린지 참가 여부 구현  

## 댓글(인증) 업로드 구현
220905 챌린지 내 인증을 위한 댓글 업로드 구현, 동영상 및 텍스트 업로드  

## 챌린지 기부를 위한 결제
220907 아임포트 API를 사용하여 결제 구현 시작  

220908 결제 기능 구현 완료  

## 마이페이지
220901 마이페이지 구성  

220909 마이페이지 결제한 내역 리스트 구현  

## 페이지 디자인
220910 디자인 시작  

220914 챌린지 페이지 디자인 수정   

220915 홈페이지, 마이페이지 수정, 카드 디자인 수정 및 로그인 후 호버메뉴 제작  

220916 디자인 마무리 및 프로젝트 완료  