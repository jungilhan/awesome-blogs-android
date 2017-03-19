# 어썸블로그 
### 개발 잡덕들을 위한 본격 고퀄리티 개발 블로그 큐레이션 서비스 

![Screenshot](https://github.com/jungilhan/awesome-blogs-android/raw/develop/screenshot.png)

대한민국의 어썸한 개발자, 기술 회사, IT 업계의 블로그 글들을 마크다운 형식으로 좀 더 편하게 읽어볼 수 있는 블로그 큐레이션 서비스입니다. 현재 제공하고 있는 어썸블로그의 전체 목록은 [이곳에서](https://github.com/BenjaminKim/awesome-blogs/blob/master/config/feeds.yml) 확인할 수 있습니다. 목록은 지속적으로 업데이트 할 예정이므로 항목은 추가되거나 삭제될 수 있습니다.

## 빌드
안드로이드 스튜디오의 Build Variants 설정에서 app 모듈을 productionDebug 또는 stagingDebug로 설정한 후 빌드할 수 있습니다.

## 풀 리퀘스트
풀 리퀘스트 시, 코드 스타일이 맞지 않아서 코드가 변경되는 부분을 피하려면 ```Preferences > Code Style > Java```에서 다음 부분을 수정하세요. 좀 더 간편한 방법은 루트 디렉토리에 있는 code-style.xml을 임포트 하는 것입니다.
  * Continuation Indent - 4
  * Field Annontations -  Do not wrap

## 서버
어썸블로그 안드로이드 앱은 아래의 서버 코드에 의해 동작하고 있습니다. 서버 코드가 궁금하신 분은 아래 링크를 방문해주세요.
 * https://github.com/BenjaminKim/awesome-blogs

## 구글 플레이 스토어
구글 플레이 스토어에서 어썸블로그 마켓 버전을 다운로드 받을 수 있습니다.
<br/>
<a href="https://play.google.com/store/apps/details?id=org.petabytes.awesomeblogs"><img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" width="20%"></a>

## 업데이트 내역
### v1.2 주스 쏟은 내 맥북 😇 2017-03-18
 * 새로운 블로그 글을 모아서 하루 한번 알림 기능 추가 (코드리뷰 @soulkeykim)
 * 새로운 서클 타입 레이아웃 추가
 * 백그라운드 피드 요청 시각화
 * 서버에 새롭게 추가된 블로그 글은 업데이트 시간과 상관없이 정렬되도록 처리 (@probepark)
 * 본문이 긴 블로그 글 진입 시 TransactionTooLargeException 현상 수정
 * Circle CI 연동 (@probepark)
 * 디버그 드로어 추가

### v1.1 개발의 완성은 얼굴 👱 2017-02-25
 * 스와이프 새로고침 추가 (@BenjaminKim)
 * 서버 API 요청 좀 더 고급지게 처리 (@ZeroBrain, @probepark)
 * 새로운 피드가 생성 됐을 때, 앱 내 알림 기능 추가
 * 상세 뷰 하단 '페이지 이동하기' 버튼으로 블로그 이동을 좀 더 쉽게 (@KimKyung-man, @BenjaminKim)
 * 'ㅎ' 폰트가 제대로 그려지지 않는 버그 수정 (@soulkeykim, @ZeroBrain)
 * 상세 뷰에서 링크 클릭 시 어쩌다 죽는 버그 수정 (@BenjaminKim)
 * 풀 리퀘스트와 버그 제보해 주신 @ZeroBrain, @probepark, @soulkeykim, @BenjaminKim, @KimKyung-man 감사합니다. 👏

### v1.0 시작이 반이라 카더라 🤗 2017-02-21
 * 어쩌다 만든 어썸블로그 마켓 배포 (@BenjaminKim)
