package hygge.blog.controller;

import hygge.blog.common.mapper.PoDtoMapper;
import hygge.blog.controller.doc.QuoteControllerDoc;
import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.blog.domain.local.dto.QuoteDto;
import hygge.blog.domain.local.po.Quote;
import hygge.blog.service.local.CacheServiceImpl;
import hygge.blog.service.local.normal.QuoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Xavier
 * @date 2022/8/1
 */
@RestController
@RequestMapping(value = "/blog-service/api/main")
public class QuoteController implements QuoteControllerDoc {
    @Autowired
    private QuoteServiceImpl quoteService;
    @Autowired
    private CacheServiceImpl cacheService;

    @Override
    @PostMapping("/quote")
    public ResponseEntity<HyggeBlogControllerResponse<QuoteDto>> createQuote(@RequestBody QuoteDto quoteDto) {
        Quote quote = PoDtoMapper.INSTANCE.dtoToPo(quoteDto);
        Quote resultTemp = quoteService.createQuote(quote);
        QuoteDto result = PoDtoMapper.INSTANCE.poToDto(resultTemp);
        return (ResponseEntity<HyggeBlogControllerResponse<QuoteDto>>) success(result);
    }

    @Override
    @PutMapping("/quote/{quoteId}")
    public ResponseEntity<HyggeBlogControllerResponse<QuoteDto>> updateQuote(@PathVariable("quoteId") Integer quoteId, @RequestBody Map<String, Object> data) {
        Quote resultTemp = quoteService.updateQuote(quoteId, data);
        QuoteDto result = PoDtoMapper.INSTANCE.poToDto(resultTemp);
        return (ResponseEntity<HyggeBlogControllerResponse<QuoteDto>>) success(result);
    }

    @Override
    @GetMapping("/quote/{quoteId}")
    public ResponseEntity<HyggeBlogControllerResponse<QuoteDto>> findQuote(@PathVariable("quoteId") Integer quoteId) {
        Quote resultTemp = quoteService.findQuoteByQuoteId(quoteId, true);
        QuoteDto result = PoDtoMapper.INSTANCE.poToDto(resultTemp);
        // userId → uid
        String authorUid = cacheService.userIdToUid(resultTemp.getUserId());
        result.setUid(authorUid);
        return (ResponseEntity<HyggeBlogControllerResponse<QuoteDto>>) success(result);
    }
}
