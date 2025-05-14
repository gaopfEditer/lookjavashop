import request from '@/utils/request'

// 分销商列表
export function getDistributorList(params?: any) {
    return request.get({ url: '/distribution/store/list', params })
}

//下拉列表
export function getOtherLists(params?: any) {
    return request.get({ url: '/distribution/level/list', params })
}

//开通分销商
export function addDistributor(params?: any) {
    return request.post({ url: '/distribution/store/open', params })
}

//关闭分销状态
export function closeDistributionStatus(params?: any) {
    return request.post({ url: '/distribution/store/freeze', params })
}

//分销等级调整
export function levelAdjust(params?: any) {
    return request.post({ url: '/distribution/store/adjustLevel', params })
}
//调整上级分销商
export function adjustLeader(data?: any) {
    return request.post({ url: '/distribution/store/adjustLeader', data })
}

//分销商详情
export function getDetail(params?: any) {
    return request.get({ url: '/distribution/store/detail', params })
}

//调整上级推荐人
export function adjustPid(params?: any) {
    return request.post({ url: '/mall.userDistribution/change_pid', params })
}

//下级列表
export function getLowerList(params?: any) {
    return request.get({ url: '/distribution/store/fansList', params })
}

//申请列表
export function applyList(params?: any) {
    return request.get({ url: '/distribution/apply/list', params })
}

//申请审核
export function postExamine(params?: any) {
    return request.post({ url: '/distribution/apply/audit', params })
}

//审核详情
export function emamineDetail(params?: any) {
    return request.get({ url: '/distribution/apply/detail', params })
}

//分销基础设置获取
export function getBaseSet(params?: any) {
    return request.get({ url: '/config/distribution_base_get', params })
}

//分销基础设置获取
export function setBaseSet(params?: any) {
    return request.post({ url: '/config/distribution_base_set', params })
}

//分销基础设置获取
export function getSettlementSet(params?: any) {
    return request.get({ url: '/config/distribution_settlement_get', params })
}

//分销基础设置获取
export function setSettlementSet(params?: any) {
    return request.post({ url: '/config/distribution_settlement_set', params })
}
//粉丝详情
export function fansInfo(params?: any) {
    return request.get({ url: '/distribution/store/fansInfo', params })
}
