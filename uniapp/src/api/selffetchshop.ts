import request from "@/utils/request";
 
//获取自提门店列表
export function getSelffetchshopList(data) {
  return request.get({ url: "/selffetchshop/list", data });
}

//获取自提门店列表
export function getSelffetchshopDetail(data: any) {
  return request.get({ url: "/selffetchshop/detail", data });
}