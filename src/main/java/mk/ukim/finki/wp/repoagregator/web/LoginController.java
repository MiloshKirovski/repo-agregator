package mk.ukim.finki.wp.repoagregator.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout) {

        if (error != null) {
            model.addAttribute("errorMessage", "Неточно корисничко име или лозинка");
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "Успешно се одјавивте");
        }

        return "login"; // Make sure this returns the correct template name
    }
//    @GetMapping("/login")
//    public String login(@RequestParam(value = "error", required = false) String error,
//                        @RequestParam(value = "logout", required = false) String logout,
//                        Model model) {
//
//        if (error != null) {
//            model.addAttribute("errorMessage", "Invalid username or password!");
//        }
//
//        if (logout != null) {
//            model.addAttribute("logoutMessage", "You have been logged out successfully!");
//        }
//
//        return "login";
//    }
}