package hygge.blog.job;

import hygge.blog.domain.local.dto.ArticleQuoteSearchCache;
import hygge.blog.domain.local.po.Quote;
import hygge.blog.job.item.QueueJobItem;
import hygge.blog.job.other.BaseBlogJob;
import hygge.blog.job.other.HyggeBlogJpaContext;
import hygge.blog.repository.database.QuoteDao;
import hygge.blog.service.elasticsearch.ElasticSearchServiceImpl;
import hygge.job.HyggeJobBatchItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Xavier
 * @date 2026/7/5
 */
@Service
public class RefreshQuoteCacheJob extends BaseBlogJob<QueueJobItem<ArticleQuoteSearchCache>, Quote, ArticleQuoteSearchCache> {
    private final QuoteDao quoteDao;
    private final ElasticSearchServiceImpl refreshElasticSearchService;

    public RefreshQuoteCacheJob(QuoteDao quoteDao, ElasticSearchServiceImpl refreshElasticSearchService) {
        this.quoteDao = quoteDao;
        this.refreshElasticSearchService = refreshElasticSearchService;
    }

    @Override
    protected String getJobName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected List<Quote> firstFetchIfNecessary(HyggeBlogJpaContext<Quote> context, HyggeJobBatchItem<QueueJobItem<ArticleQuoteSearchCache>> jobBatchItem) {
        Pageable pageable = PageRequest.of(0, context.getBatchSize(), Sort.by(Sort.Order.asc("quoteId")));
        Page<Quote> page = quoteDao.findAll(pageable);
        context.setPage(page);

        if (page.isLast()) {
            context.setNoNextPage(true);
        }

        return page.getContent();
    }

    @Override
    protected List<Quote> getNextBatch(HyggeBlogJpaContext<Quote> context, HyggeJobBatchItem<QueueJobItem<ArticleQuoteSearchCache>> jobBatchItem) {
        if (context.isNoNextPage()) {
            return List.of();
        }

        Page<Quote> page = context.getPage();
        page = quoteDao.findAll(page.nextPageable());
        context.setPage(page);

        if (page.isLast()) {
            context.setNoNextPage(true);
        }

        return page.getContent();
    }

    @Override
    protected QueueJobItem<ArticleQuoteSearchCache> createJobItem(HyggeBlogJpaContext<Quote> context, HyggeJobBatchItem<QueueJobItem<ArticleQuoteSearchCache>> jobBatchItem, Quote rawData) {
        return new QueueJobItem<>(rawData);
    }

    @Override
    protected ArticleQuoteSearchCache handleSingleItem(HyggeBlogJpaContext<Quote> context, QueueJobItem<ArticleQuoteSearchCache> jobItem) {
        Quote quote = jobItem.getRawData();
        return refreshElasticSearchService.buildEsDto(quote);
    }

    @Override
    protected void batchCompleteHook(HyggeBlogJpaContext<Quote> context, HyggeJobBatchItem<QueueJobItem<ArticleQuoteSearchCache>> jobBatchItem, List<Quote> rawDataCollection, List<ArticleQuoteSearchCache> processedDataCollection) {
        refreshElasticSearchService.save(processedDataCollection);
        super.batchCompleteHook(context, jobBatchItem, rawDataCollection, processedDataCollection);
    }
}
