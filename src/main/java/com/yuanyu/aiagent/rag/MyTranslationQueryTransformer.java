package com.yuanyu.aiagent.rag;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yuanyu.aiagent.util.translation.TransApi;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class MyTranslationQueryTransformer implements QueryTransformer {
    private static final Logger logger = LoggerFactory.getLogger(MyTranslationQueryTransformer.class);


    private final String targetLanguage;
    private final String APP_ID;
    private final String SECURITY_KEY;

    /**
     * 翻译查询
     * @param query
     * @return
     */
    public Query transform(Query query) {
        Assert.notNull(query, "query cannot be null");
        logger.debug("Translating query to target language: {}", this.targetLanguage);
        TransApi transApi = new TransApi(this.APP_ID, this.SECURITY_KEY);
        String jsonResult = transApi.getTransResult(query.text(), "auto", this.targetLanguage);
        String translatedQueryText = getTranslateResult(jsonResult);

        if (!StringUtils.hasText(translatedQueryText)) {
            logger.warn("翻译结果为空，返回原文");
            return query;
        } else {
            return query.mutate().text(translatedQueryText).build();
        }
    }

    /**
     * 获取JSON中的翻译结果
     * @param jsonResult
     * @return
     */
    private String getTranslateResult(String jsonResult) {
        JSONObject jsonObject = JSONUtil.parseObj(jsonResult);
        String unicodeTranslatedResult = jsonObject.getJSONArray("trans_result").getJSONObject(0).getStr("dst");
        if (StrUtil.isBlank(unicodeTranslatedResult)) {
            logger.warn("翻译结果为空，请查看返回的JSON字符串是否有误");
            return null;
        }
        return StrUtil.toString(unicodeTranslatedResult);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String targetLanguage;
        private String APP_ID;
        private String SECURITY_KEY;

        private Builder() {
        }

        public Builder targetLanguage(String targetLanguage) {
            this.targetLanguage = targetLanguage;
            return this;
        }
        public Builder APP_ID(String APP_ID) {
            this.APP_ID = APP_ID;
            return this;
        }
        public Builder SECURITY_KEY(String SECURITY_KEY) {
            this.SECURITY_KEY = SECURITY_KEY;
            return this;
        }

        public MyTranslationQueryTransformer build () {
            Assert.hasText(targetLanguage, "请配置目标语言");
            Assert.hasText(APP_ID, "请配置APP_ID");
            Assert.hasText(SECURITY_KEY, "请配置SECURITY_KEY");
            return new MyTranslationQueryTransformer(this.targetLanguage, this.APP_ID, this.SECURITY_KEY);
        }
    }

}
