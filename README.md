fisherman-pushserver
====================

### [소스 설명]
-  dockerfile : 도커 설정 파일
- src : 코틀린 소스
- resources : 기본 리소스
- resources-$profole : 각 단계별 설정 리소스
- resources/application.conf : 기본 설정 - 자동생성 & 개인설정 추가 가능
- resources/i18n/$lang.conf : 다국어 메세지 설정 파일

<br/>

### [쉘 스크립트 설명]
-  build.sh : 빌드 슼립트 - Test 소스에 오류가 있어서 에러가 날 수 있음 - 수정 예정
-  start.sh : 서버 start
-  stop.sh  : 서버 stop
-  docker.sh : 도커 이미지 생성 스크립트 - 작성중
-  kafka/kafka.sh : kafka 명령 모음 스크립트


<br/>
-  실행 명령 :  gradle -Pprofile=local run

<br/>
-  혹시 소스에 문의사항이 있으시면 저(Kepha)에게 DM  이나 메일 주세요 ~~~
