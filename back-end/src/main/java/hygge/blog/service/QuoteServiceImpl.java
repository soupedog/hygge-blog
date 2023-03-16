package hygge.blog.service;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.dao.QuoteDao;
import hygge.blog.domain.bo.BlogSystemCode;
import hygge.blog.domain.dto.QuoteDto;
import hygge.blog.domain.dto.QuoteInfo;
import hygge.blog.domain.enums.QuoteStateEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.mapper.MapToAnyMapper;
import hygge.blog.domain.mapper.OverrideMapper;
import hygge.blog.domain.mapper.PoDtoMapper;
import hygge.blog.domain.po.Quote;
import hygge.blog.domain.po.User;
import hygge.blog.elasticsearch.service.RefreshElasticSearchServiceImpl;
import hygge.commons.constant.enums.ColumnTypeEnum;
import hygge.commons.exception.LightRuntimeException;
import hygge.util.UtilCreator;
import hygge.util.bo.ColumnInfo;
import hygge.util.definition.DaoHelper;
import hygge.web.template.HyggeWebUtilContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Xavier
 * @date 2022/8/2
 */
@Slf4j
@Service
public class QuoteServiceImpl extends HyggeWebUtilContainer {
    private static final DaoHelper daoHelper = UtilCreator.INSTANCE.getDefaultInstance(DaoHelper.class);
    @Autowired
    private QuoteDao quoteDao;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RefreshElasticSearchServiceImpl refreshElasticSearchService;

    private static final Collection<ColumnInfo> forUpdate = new ArrayList<>();

    static {
        forUpdate.add(new ColumnInfo("imageSrc", null, ColumnTypeEnum.STRING, true, true, 0, 1000));
        forUpdate.add(new ColumnInfo("content", null, ColumnTypeEnum.STRING, true, false, 1, 5000));
        forUpdate.add(new ColumnInfo("source", null, ColumnTypeEnum.STRING, true, true, 0, 2000));
        forUpdate.add(new ColumnInfo("portal", null, ColumnTypeEnum.STRING, true, true, 0, 2000));
        forUpdate.add(new ColumnInfo("remarks", null, ColumnTypeEnum.STRING, true, true, 0, 5000));
        forUpdate.add(new ColumnInfo("orderVal", null, ColumnTypeEnum.INTEGER, true, true, Integer.MIN_VALUE, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo("quoteState", null, ColumnTypeEnum.STRING, true, false, 1, 50));
    }

    public Quote createQuote(Quote quote) {
        parameterHelper.stringNotEmpty("content", (Object) quote.getContent());

        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        quote.setQuoteId(null);
        quote.setQuoteState(parameterHelper.parseObjectOfNullable("quoteState", quote.getQuoteState(), QuoteStateEnum.ACTIVE));
        quote.setUserId(currentUser.getUserId());

        Quote result = quoteDao.save(quote);

        CompletableFuture.runAsync(() -> {
            refreshElasticSearchService.freshSingleQuote(result.getQuoteId());
        }).exceptionally(e -> {
            log.error("刷新句子(" + result.getQuoteId() + ") 模糊搜索数据 失败.", e);
            return null;
        });

        return result;
    }

    public Quote updateQuote(Integer quoteId, Map<String, Object> data) {
        parameterHelper.integerFormatNotEmpty("quoteId", quoteId);

        HashMap<String, Object> finalData = daoHelper.filterOutTheFinalColumns(data, forUpdate);

        Quote old = findQuoteByQuoteId(quoteId, false);

        Quote newOne = MapToAnyMapper.INSTANCE.mapToQuote(finalData);

        OverrideMapper.INSTANCE.overrideToAnother(newOne, old);

        Quote result = quoteDao.save(old);

        CompletableFuture.runAsync(() -> {
            refreshElasticSearchService.freshSingleQuote(result.getQuoteId());
        }).exceptionally(e -> {
            log.error("刷新句子(" + result.getQuoteId() + ") 模糊搜索数据 失败.", e);
            return null;
        });

        return result;
    }

    public QuoteInfo findQuoteInfo(int currentPage, int pageSize) {
        Example<Quote> example = Example.of(Quote.builder().quoteState(QuoteStateEnum.ACTIVE).build());

        Sort sort = Sort.by(Sort.Order.desc("orderVal"), Sort.Order.desc("createTs"));
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<Quote> resultTemp = quoteDao.findAll(example, pageable);

        QuoteInfo result = QuoteInfo.builder().build();

        List<QuoteDto> list = collectionHelper.filterNonemptyItemAsArrayList(false, resultTemp.getContent(), (PoDtoMapper.INSTANCE::poToDto));
        result.setQuoteList(list);
        result.setTotalCount(resultTemp.getTotalElements());
        return result;
    }

    public Quote findQuoteByQuoteId(Integer quoteId, boolean nullable) {
        Optional<Quote> resultTemp = quoteDao.findById(quoteId);
        return checkQuoteResult(resultTemp, quoteId, nullable);
    }

    private Quote checkQuoteResult(Optional<Quote> quoteTemp, Integer quoteId, boolean nullable) {
        if (!nullable && quoteTemp.isEmpty()) {
            throw new LightRuntimeException(String.format("Quote(%d) was not found.", quoteId), BlogSystemCode.QUOTE_NOT_FOUND);
        }
        return quoteTemp.orElse(null);
    }

}
