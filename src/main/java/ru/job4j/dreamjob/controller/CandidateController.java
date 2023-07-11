package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.repository.CandidateRepository;
import ru.job4j.dreamjob.repository.MemoryCandidateRepository;

@Controller
@RequestMapping("/candidates")
public class CandidateController {

    private final CandidateRepository candidateRepository = MemoryCandidateRepository.getInstance();

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidateRepository.findAll());
        return "candidates/list";
    }

    /**
     * В данном методе аннотация @GetMapping
     * здесь говорит SpringBoot, что по
     * заданному URI (/candidates/create)
     * нужно обслужить HTTP GET запрос.
     *
     * <p>GET метод браузер использует
     * при открытии страниц.
     */
    @GetMapping("/create")
    public String getCreationPage() {
        return "candidates/create";
    }
}
