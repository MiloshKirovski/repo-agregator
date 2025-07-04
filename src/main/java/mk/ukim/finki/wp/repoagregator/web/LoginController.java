package mk.ukim.finki.wp.repoagregator.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout) {

        if (error != null && error.equals("true")) {
            model.addAttribute("errorMessage", "Неточно корисничко име или лозинка");
        }

        if (logout != null && logout.equals("true")) {
            model.addAttribute("logoutMessage", "Успешно се одјавивте");
        }

        return "login";
    }

}