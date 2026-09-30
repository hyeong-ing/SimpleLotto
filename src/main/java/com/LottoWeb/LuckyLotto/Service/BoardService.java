package com.LottoWeb.LuckyLotto.Service;

import com.LottoWeb.LuckyLotto.DB.BoardRepository;
import com.LottoWeb.LuckyLotto.DB.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public void saveComment(String content) {
        if (content == null) {
            return;
        }

        String trimmedContent = content.trim();

        if (trimmedContent.length() >= 1 && trimmedContent.length() <= 25) {
            boardRepository.save(new Comment(trimmedContent));
        }
    }


    public Page<Comment> get10Comments() {
        Pageable pageable = PageRequest.of(0,10);
        return boardRepository.findAllByOrderByIdDesc(pageable);
    }
}
