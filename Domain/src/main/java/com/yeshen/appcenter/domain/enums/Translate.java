package com.yeshen.appcenter.domain.enums;

import com.yeshen.appcenter.domain.common.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Date 2022/07/11  19:43
 * author  by HuBingKuan
 */
@Getter
@AllArgsConstructor
public enum Translate {
    CN("cn", "{appName}电脑版下载_{appName}PC电脑版下载_夜神安卓模拟器", "怎么在电脑上玩{appName}手游,{appName}电脑版下载,{appName}模拟器下载,{appName}PC版",
            "玩{appName}手游电脑版,下载夜神模拟器享受端游高清画质,键鼠操作简单上手,兼容性好不卡帧",
            "喜欢{appName}，还在盯着手机的小屏幕吗？夜神模拟器助你像职业电竞选手一般电脑大屏幕观赏，用键盘，鼠标和手柄来全面操控你的游戏。在电脑上下载,安装{appName}并流畅玩耍。夜神模拟器帮你解除电量不足，流量消耗以及来电时被强行打断的困扰。最新的夜神安卓模拟器完美兼容安卓7，适配99%的安卓手游，是你在电脑上畅玩安卓手游及APP的最强助手。我们亲自体验并适配最顺手的按键映射，让{appName}更还原电脑游戏的操作习惯；比游戏更懂你。<br/>夜神模拟器多开功能，让多款应用及游戏同时飞起，生活娱乐两不误。夜神独家核心虚拟化技术完美兼容AMD及Intel电脑，让你的电脑运行更稳定，流畅。夜神模拟器助你游戏超神，还在等什么，快下载体验吧！",
            "https://www.yeshen.com/appcenter/", "https://www.yeshen.com/download/fullPackage"),

    TW("tw", "{appName} ", "",
            "下載安裝{appName}電腦PC版，支援鍵盤和滑鼠操控，穩定多開不卡頓，在電腦上大螢幕上免費暢玩各類手機遊戲",
            "喜歡{appName}，還在盯著手機的小螢幕嗎？夜神模擬器助你像職業電競選手一般電腦大螢幕觀賞，用鍵盤，滑鼠和手把來全面操控你的遊戲。在電腦上下載,安裝{appName}並流暢體驗。夜神模擬器讓你免於電量不足，流量消耗以及通話來電時被強行打斷的困擾。最新的夜神安卓模擬器完美相容安卓7，支援99%的安卓手遊，是你在電腦上暢玩安卓手遊及APP的最強助手。我們親自體驗並客製最順手的按鍵設定，讓{appName}更還原電腦遊戲的操作習慣；比遊戲更懂你。<br/>夜神模擬器多開功能，讓多款應用及遊戲帳號同時飛起，生活娛樂兩不誤。夜神獨家核心虛擬化技術完美相容AMD及Intel電腦，讓你的電腦運行更穩定，流暢。夜神模擬器位你遊戲助攻，還在等什麼，快下載體驗吧！",
            "https://tw.bignox.com/appcenter/", "https://{region}.bignox.com/download/fullPackage"),


    EN("en", "Download & Play {appName} on PC with ", "",
            "Download & play {appName} on PC with NoxPlayer(emulator). Play games with a big screen and keyboard! {shortDesc}",
            "Do you wanna run {appName} with a better gaming experience? With the benefit of the bigger screen, smarter keyboard and the higher hardware performance, NoxPlayer brings you an extreme gaming experience on PC. By downloading and playing {appName} on PC via NoxPlayer, users don't need to worry about the battery or the interruption of calling.<br/>NoxPlayer is compatible with Android 7 and supports running over 90% of the mobile games on PC, which will boost your gaming experience perfectly. In addition, by opening multiple instances, Noxplayer supports to running multiple games or apps at the same time, or chatting with your friend while playing game.<br/>NoxPlayer is perfectly compatible with AMD and Intel with the exclusive core virtualization technology, making your computer run more stable and smoothly. Download NoxPlayer and experience it now!",
            "https://www.bignox.com/appcenter/", "https://{region}.bignox.com/download/fullPackage"),

    JP("jp", "{appName}をPCでダウンロード・遊ぶ方法 - ", "",
            "{appName}をPCでダウンロード、PCの大画面でスマホ以上な品質を堪能！PCでプレイすると、小さい画面で操作しづらい問題が解消！空き容量やバッテリーに心配する必要がなく、PCでのしながらプレイも可能になります！キーボードやマウスを用いて、NoxPlayerでスマホゲームを堪能しましょう！",
            "{appName}をプレイするには、スマホの小画面ではやりづらいとでも思われるプレイヤーにNoxPlayerをご紹介します！NoxPlayerでプレイのメリットがPCの大画面だけではなく、キーボードもマウスもコントローラーも対応というところもあります！{appName}をPCでダウンロードし、プレイすることでバッテリー消耗激しい、空き容量不足、着信などによる邪魔という心配も解消されます。Android7に対応した最新版のNoxPlayerでは、99%のスマホゲームがプレイできます。スマホアプリやスマホゲームをPCで起動させるための最強かつ一番軽いAndroidエミュレーターです。スマホ以上の操作感を実現しています。当然ながら、NoxPlayerに内蔵されているのがオープンソースの純正AndroidOSとなっており、安全性もバッチリです。<br/>NoxPlayerのマルチプレイ機能を使うことで、多重起動そして複数ゲームの同時プレイが可能になります。独自な仮想化技術、そしてAMDやIntelとの高い互換性を備え、一番軽くて安定なプレイ体験を届いています。今すぐダウンロードしましょう！",
            "https://jp.bignox.com/appcenter/", "https://{region}.bignox.com/download/fullPackage"),

    KR("kr", "{appName} PC에서 렉없이 즐기는 가벼운 Android 앱플레이어 - ", "",
            "PC에서 렉없는 앱플레이어 녹스로 {appName} 다운로드하여 큰 스크린에서 발열 현상 없이 자유로운 가상키 설정과 게임패드로 더 재미있는 게임을 만날 수 있습니다.",
            "{appName}, 아직도 핸드폰으로 {appName} 플레이하고 계시나요? 녹스 앱플레이어로 {appName} 플레이하면 더 큰 스크린으로 게임을 체험할 수 있으며 키보드, 마우스를 이용해 더 완벽하게 게임을 컨트롤할 수 있습니다. PC로 녹스에서 {appName} 게임 다운로드 및 설치하고 핸드폰 배터리 용량을 인한 발열현상을 걱정 안하셔도 되니까 매우 편할 겁니다.<br/>최신버전의 녹스 앱플레이어에서는 호환성과 안전성이 완벽한 안드로이드 7버전을 지원되며 완벽한 게임 플레이  만나게 될 겁니다. 게임 유저의 입장에서 설정된 맞춤형 가상키 세팅을 통해서 마침 PC 게임 플레이하고 있는 것처럼 모바일 게임을 플레이하게 될 겁니다.<br/>녹스 앱플레이어에서 멀티 플레이도 지원 가능합니다. 여러 앱과 게임 동시에 실행 가능하며 많은 즐거움을 동시에 누릴 수 있습니다. 녹스는 최강의 안드로이드 모바일 에뮬레이터로써 AMD, Intel 기기와 완벽한 호환성을 가지고 있기에 부드럽고 가벼운 녹스에서 최상의 게임 체험 만나볼수 있을 겁니다. 녹스 앱플레이어, PC에서 즐기는 모바일 라이프! 지금 바로 다운로드하세요!",
            "https://kr.bignox.com/appcenter/", "https://{region}.bignox.com/download/fullPackage"),


    TH("th", "ดาวน์โหลดและเล่น {appName} บน PC ด้วย ", "",
            "ดาวน์โหลดและเล่น {appName} ฟรีบนคอมด้วย NoxPlayer อีมูเลเตอร์ Android ดีที่สุด-ด้วยจอใหญ่แป้นพิมพ์เมาส์และเกมแพด ไม่ต้องกังวลกับมือถือร้อนอีกแล้ว! เล่นเกมออนไลน์ง่ายขึ้น ลองเล่นเดี๋ยวนี้!",
            "ดาวน์โหลดและเล่น {appName} บน PC โดยใช้ NoxPlayer โปรแกรมจำลอง NoxPlayer จะให้คุณเล่นเกมบน PC ด้วยหน้าจอใหญ่ โดยใช้การควบคุมแป้นพิมพ์และเกมแพด ให้คุณเล่นเกมได้เร็วขึ้นสนุกขึ้น ติดตั้งและดาวน์โหลด {appName} บน PC โดยใช้ NoxPlayer โปรแกรมจำลอง NoxPlayer จะช่วยคุณได้พลังงานต่ําการสูญเสียการไหลและการหยุดชะงักของสายเรียกเข้า มันเข้ากันได้กับ Android 7 และ 99% ของเกมมือถือ Android อย่างสมบูรณ์แบบ มันเป็นผู้ช่วยที่ทรงพลังที่สุดสําหรับคุณในการเล่นเกมมือถือ Android และแอพได้อย่างอิสระบน PC ของคุณ เรากำหนดการควบคุมแป้นพิมพ์อย่างเป็นอาชีพ คืนค่าประสบการณ์การใช้งานคอมพิวเตอร์รุ่น {appName} รู้จักคุณดีกว่าเกม <br/>ฟังก์ชั่น Multi-Drive ของ NoxPlayer ให้คุณเล่นเกมโดยเปิดหลายหน้าจอพร้อมกัน เทคโนโลยีการจำลองเสมือนหลักเฉพาะ ของ NOX เข้ากันได้ดีกับคอมพิวเตอร์ AMD และ Intel ทำให้คอมพิวเตอร์ของคุณทำงานได้เสถียรและราบรื่นยิ่งขึ้น เล่นเกมขอให้สนุกใช้ NoxPlayer ด้วยกัน ดาวน์โหลดทันทีเลย",
            "https://th.bignox.com/appcenter/", "https://{region}.bignox.com/download/fullPackage"),


    VN("vn", "Tải và chơi {appName} trên PC (máy tính) cùng ", "",
            "Cách tải {appName} trên PC/máy tính nhanh- mượt- ổn định cùng NoxPlayer giả lập android. Chơi {appName} phiên bản PC với màn hình lớn, đồ họa đẹp mắt, thao tác dễ dàng với bàn phím và chuột, leo rank nhanh chóng. ",
            "Chơi {appName}, bạn vẫn đang nhìn chằm chằm vào màn hình điện thoại ư? Giả lập NoxPlayer sẽ giúp bạn trở thành một game thủ chuyên nghiệp để chơi các tựa game bạn yêu thích trên máy tính, điều khiển trò chơi thông qua bàn phím, chuột máy tính và tay cầm, mọi thao tác sẽ trở nên dễ dàng và linh hoạt hơn bao giờ hết, giúp cho bạn tăng cấp, leo rank nhanh hơn. Tải và chơi {appName} một cách ổn định và mượt mà nhất trên máy tính cùng giả lập NoxPlayer. Chơi game mobile trên NoxPlayer sẽ giúp bạn tránh được những hao mòn và tổn hại cho điện thoại, không còn phải sợ các cuộc gọi đến bị cản trở khi chơi game. Bản mới nhất của NoxPlayer hỗ trợ tốt cho Android 7 và tương thích một cách hoàn hảo với hơn 99% các tựa game mobile hiện hành. Nox chính là vũ khí tối ưu nhất giúp bạn chơi tốt các trò chơi hay ứng dụng mobile trên máy tính. Chúng tôi hỗ trợ cài đặt bàn phím điều khiển cho từng game một cách phù hợp nhất, để bạn có thể trải nghiệm {appName} một cách chân thực nhất với các thao tác như chơi trên mobile. <br/>NoxPlayer hỗ trợ tính năng Trình đa nhiệm Multidriver, có thể khởi tạo và chơi nhiều cửa sổ giả lập và acc cùng lúc, bạn có thể đồng thời chơi game/ứng dụng yêu thích trên các cửa sổ được tạo bởi NoxPlayer. Kỹ thuật ảo hóa độc quyền từ NoxPlayer tương thích hoàn hảo với máy tính AMD và Intel, giúp cho máy tính vận hành ổn định và mượt mà. Giả lập NoxPlayer sẽ biến bạn thành các game thủ hạng nặng, còn chần chờ gì nữa, hãy tải ngay NoxPlayer về máy tính và trải nghiệm nhé!",
            "https://vn.bignox.com/appcenter/", "https://{region}.bignox.com/download/fullPackage"),

    ID("id", "Download {appName} di PC dengan ", "",
            "Download & mainkan {appName} di PC dengan NoxPlayer(emulator). {shortDesc}",
            "Ingin menjalankan {appName} dengan pengalaman bermain game yang lebih baik? Dengan keunggulan layar yang lebih besar, keyboard yang lebih cerdas dan stabil, serta kinerja hardware yang lebih unggul, dengan bangga NoxPlayer menciptakan pengalaman bermain game yang ekstrem di PC. Dengan mengunduh dan memainkan {appName} di PC melalui NoxPlayer, pengguna tidak perlu khawatir dengan kapasitas baterai atau gangguan panggilan lainnya.<br/>NoxPlayer kompatibel dengan Android 7 dan mensupport lebih dari 90% game mobile di PC, yang akan meningkatkan pengalaman bermain game Anda dengan sempurna. Selain itu, dengan membuka banyak layar instance, Noxplayer mendukung untuk menjalankan beberapa game atau aplikasi secara bersamaan, atau mengobrol dengan teman Anda saat bermain game.<br/>NoxPlayer sangat kompatibel dengan AMD dan Intel dengan teknologi virtualisasi inti eksklusif, membuat komputer Anda berjalan lebih stabil dan lancar. Unduh NoxPlayer dan rasakan pengalaman ekstremnya sekarang!",
            "https://id.bignox.com/appcenter/", "https://{region}.bignox.com/download/fullPackage"),

    RU("ru", "Загрузите {appName} на ПК с помощью ", "",
            "Загрузите & играйте в {appName} на ПК с помощью NoxPlayer. {shortDesc}",
            "Хотите ли вы запустить {appName} с лучшим игровым опытом? Благодаря большому экрану, более умной клавиатуре и более высокой производительности оборудования NoxPlayer предлагает вам невероятные игровые возможности на ПК. Загружая и играя в {appName} на ПК через NoxPlayer, пользователям не нужно беспокоиться о разряде батареи или прерывании звонка.\\n\\nNoxPlayer совместим с Android 7 и поддерживает запуск более 90% мобильных игр на ПК, что значительно улучшит ваш игровой опыт. Кроме того, открывая несколько экземпляров, Noxplayer поддерживает одновременный запуск нескольких игр или приложений или общение с другом во время игры.\\n\\nNoxPlayer полностью совместим с AMD и Intel благодаря эксклюзивной технологии виртуализации ядра, что делает работу вашего компьютера более стабильной и плавной. Загрузите NoxPlayer и испытайте его сейчас!",
            "https://ru.bignox.com/appcenter/", "https://{region}.bignox.com/download/fullPackage"),


    ES("es", "Descargar {appName} en PC con ", "",
            "Descarga & juega a {appName} en PC con NoxPlayer(emulador). {shortDesc}",
            "¿Quieres ejecutar {appName} con una mejor experiencia? Con el beneficio de una pantalla más grande, un teclado más inteligente y un mayor rendimiento del hardware, NoxPlayer te ofrece una experiencia de juego extrema en la PC. Al descargar y jugar {appName} en la PC a través de NoxPlayer, los usuarios no No necesita preocuparse por la batería o la interrupción de la llamada.\\n\\nNoxPlayer es compatible con Android 7 y admite la ejecución de más del 90% de los juegos móviles en PC, lo que mejorará tu experiencia de juego a la perfección. Además, al abrir varias instancias, Noxplayer admite la ejecución de varios juegos o aplicaciones al mismo tiempo, o chatear con tu amigo mientras juegas.\\n\\nNoxPlayer es perfectamente compatible con AMD e Intel con la exclusiva tecnología de virtualización central, lo que hace que tu computadora funcione de manera más estable y sin problemas. ¡Descarga NoxPlayer y experiméntalo ahora!",
            "https://es.bignox.com/appcenter/", "https://{region}.bignox.com/download/fullPackage"),

    PT("pt", "Baixar {appName} no PC com ", "",
            "Baixe e jogue {appName} no PC com NoxPlayer (emulador). {shortDesc}",
            "Você quer rodar {appName} com uma experiência de jogo melhor? Com \u200B\u200Bo benefício de uma tela maior, teclado mais inteligente e desempenho de hardware superior, NoxPlayer oferece uma experiência de jogo extrema no PC. Ao baixar e jogar {appName} no PC via NoxPlayer, os usuários não precisa se preocupar com a bateria ou a interrupção da chamada.\\n\\nNoxPlayer é compatível com Android 7 e suporta a execução de mais de 90% dos jogos para celular no PC, o que irá melhorar sua experiência de jogo perfeitamente. Além disso, ao abrir várias instâncias, Noxplayer oferece suporte para executar vários jogos ou aplicativos ao mesmo tempo, ou conversar com seu amigo durante o jogo.\\n\\nNoxPlayer é perfeitamente compatível com AMD e Intel com a tecnologia de virtualização de núcleo exclusiva, tornando seu computador mais estável e suave. Baixe NoxPlayer e experimente agora!",
            "https://pt.bignox.com/appcenter/", "https://{region}.bignox.com/download/fullPackage"),

    ;

    private String region;
    private String seoTitle;
    private String seoKeywords;
    private String seoDescription;
    private String appShortDesc;
    private String domainUrl;
    private String downloadUrl;

    public static Translate getTranslateByRegion(String region) {
        Optional<Translate> optional = Arrays.stream(Translate.values()).filter(e -> e.getRegion().equals(region)).findFirst();
        if(optional.isPresent()){
            return optional.get();
        }else{
            throw new BusinessException(ResultCode.ASSERT_ERROR,"region参数错误");
        }
    }
}