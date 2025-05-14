import { getWxCodeUrl, OALogin } from "@/api/account";
import { wxJsConfig } from "@/api/app";
import { isAndroid } from "./client";
import cache from "@/utils/cache";
import { SHARE_CODE } from "@/enums/cacheEnums";
import wx from "weixin-js-sdk";
import { log10 } from "mathjs";

const wechatOa = {
  config() {
    return new Promise((resolve, reject) => {
      wxJsConfig({
        url: this.getSignLink(),
      }).then((res) => {
        wx.config({
          ...res,
          success: () => {
            resolve("success");
          },
          fail: (res: any) => {
            reject("wx config is fail");
          },
        });
      });
    });
  },
  getSignLink() {
    if (typeof window.signLink === "undefined" || window.signLink === "") {
      window.signLink = location.href.split("#")[0];
    }
    return isAndroid() ? location.href.split("#")[0] : window.signLink;
  },
  getUrl() {
    getWxCodeUrl().then((res) => {
      location.href = res.url;
    });
  },
  authLogin(code: string) {
    return new Promise((resolve, reject) => {
      const share_code = cache.get(SHARE_CODE) || "";
      OALogin({
        code,
        inviteCode: share_code,
      })
        .then((res) => {
          cache.remove(SHARE_CODE);
          resolve(res);
        })
        .catch((err) => {
          cache.remove(SHARE_CODE);
          reject(err);
        });
    });
  },
  miniProgram: wx.miniProgram,
  ready() {
    return new Promise((resolve) => {
      wx.ready(() => {
        resolve("success");
      });
    });
  },
  pay(options: Record<any, any>) {
    return new Promise((resolve, reject) => {
      this.ready()
        .then(() => {
          wx.chooseWXPay({
            timestamp: options.timeStamp,
            nonceStr: options.nonceStr,
            package: options.packageValue,
            signType: options.signType,
            paySign: options.paySign,
            success: (res: any) => {
              if (res.errMsg === "chooseWXPay:ok") {
                resolve(res);
              } else {
                reject(res.errMsg);
              }
            },
            cancel: (res: any) => {
              reject(res);
            },
            fail: (res: any) => {
              reject(res);
            },
          });
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
  async share(options: Record<any, any>): Promise<void> {
    return new Promise((resolve, reject) => {
      this.ready()
        .then(() => {
          const { title, link, imgUrl, desc } = options;
          const shareApi = [
            "updateTimelineShareData",
            "updateAppMessageShareData",
          ];
          for (const api of shareApi) {
            wx[api]({
              title: title,
              link: link,
              imgUrl: imgUrl,
              desc: desc,
              success() {
                resolve();
              },
              fail() {
                reject();
              },
            });
          }
        })
        .catch(reject);
    });
  },
  getAddress() {
    return new Promise((reslove, reject) => {
      this.ready().then(() => {
        wx.openAddress({
          success: (res: any) => {
            reslove(res);
          },
          fail: (res: any) => {
            reject(res);
          },
        });
      });
    });
  },
  getLocation() {
    return new Promise((reslove, reject) => {
      this.ready().then(() => {
        wx.getLocation({
          type: "gcj02",
          success: (res: any) => {
            reslove(res);
          },
          fail: (res: any) => {
            reject(res);
          },
        });
      });
    });
  },

  hideMenuItems(menuList: string[]) {
    return new Promise((resolve, reject) => {
      this.ready().then(() => {
        wx.hideMenuItems({
          menuList,
          success: (res: any) => {
            resolve(res);
          },
          fail: (res: any) => {
            reject(res);
          },
        });
      });
    });
  },
  showMenuItems(menuList: string[]) {
    return new Promise((resolve, reject) => {
      this.ready().then(() => {
        wx.showMenuItems({
          menuList,
          success: (res: any) => {
            resolve(res);
          },
          fail: (res: any) => {
            reject(res);
          },
        });
      });
    });
  },
};

export default wechatOa;
