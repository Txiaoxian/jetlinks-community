package org.jetlinks.community.rule.engine.log;

import org.hswebframework.ezorm.core.param.QueryParam;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.jetlinks.community.rule.engine.entity.RuleEngineExecuteEventInfo;
import org.jetlinks.community.rule.engine.entity.RuleEngineExecuteLogInfo;
import reactor.core.publisher.Mono;

/**
 * 规则引擎日志服务,用于查询规则执行日志信息
 *
 * @since 1.8
 */
public interface RuleEngineLogService {

    /**
     * 分页查询规则执行事件日志
     *
     * @param queryParam 查询参数
     * @return 分页查询结果
     */
    Mono<PagerResult<RuleEngineExecuteEventInfo>> queryEvent(QueryParam queryParam);

    /**
     * 分页查询规则日志
     *
     * @param queryParam 查询参数
     * @return 分页查询结果
     */
    Mono<PagerResult<RuleEngineExecuteLogInfo>> queryLog(QueryParam queryParam);

}
