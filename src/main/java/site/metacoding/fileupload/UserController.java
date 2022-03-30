package site.metacoding.fileupload;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @GetMapping("/main")
    public String main(Model model) {
        User user = userRepository.findById(1).get();
        model.addAttribute("user", user);
        return "main";
    }

    @PostMapping("/join")
    public String join(JoinDto joinDto) { // joindto로 받을거야!! form tag로 전송할거라서 바로 읽을 수 있다. (버퍼로 읽는 것은 두가지 밖에 없다-json,
                                          // 문자(있는그대로 받고싶을때))

        UUID uuid = UUID.randomUUID();

        String requestFileName = joinDto.getFile().getOriginalFilename();

        // ab235225-22fe-df34-sdlf2-242451525_a.png
        String imgurl = uuid + "_" + requestFileName;

        // 메모리에 있는 파일 데이터를 파일 시스템으로 옮겨야함.
        // 1. 빈 파일 하나 생성 haha.png
        // File file = new File("d\\example\\file.txt");

        // 2. 빈 파일에 스트림 연결
        // 3. for문 돌리면서 바이트로 쓰면 된다. (FileWriter 객체!)
        try {
            // 1. folder가 미리 만들어져있어야한다.
            // 2. (os관점) 리눅스: / , 윈도우: \ 하지만 이건 path객체가 정하는 것이다. 그래서 뭔지 테스트 해봐야됨
            // imageUrl = a.png (db에는 경로를 저장하지 않고 파일명만 저장해야한다. 경로는 계속 바뀔 수 있으니까 , 경로는 전역적으로
            // 잡아줘야한다. )
            // 3. 윈도우: c:/upload/ 리눅스: c:/upload/
            // 우리는 상대경로를 사용할 예정!!
            Path filePath = Paths.get("src/main/resources/static/upload/" + imgurl);
            Files.write(filePath, joinDto.getFile().getBytes());

            userRepository.save(joinDto.toEntity(imgurl));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "joinComplete"; // view resolver 발동시키기 위해서는,
    }
}
