package com.LottoWeb.LuckyLotto.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

@Controller
public class RandomController {

    private final Random rd = new Random();

    @GetMapping("/")
    public String firstPage() {
        return "button";
    }


    @GetMapping("/Random1")
    public String random1(Model model) {
        List<Integer> lottoList = createLottoNumbers();

        model.addAttribute("Number1", lottoList.get(0));
        model.addAttribute("Number2", lottoList.get(1));
        model.addAttribute("Number3", lottoList.get(2));
        model.addAttribute("Number4", lottoList.get(3));
        model.addAttribute("Number5", lottoList.get(4));
        model.addAttribute("Number6", lottoList.get(5));

        return "button1";
    }

    private List<Integer> createLottoNumbers() {
        Set<Integer> lottoSet = new TreeSet<>();

        while (lottoSet.size() < 6) {
            int ranNum = rd.nextInt(45) + 1;
            lottoSet.add(ranNum);
        }

        return new ArrayList<>(lottoSet);
    }

}
