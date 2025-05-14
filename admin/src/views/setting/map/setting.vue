<template>
    <div class="website-information">
        <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" size="small">
            <!-- 分享设置 -->
            <el-card shadow="never" class="!border-none">
                <div class="text-xl font-medium mb-[20px]">地图设置</div>
                    <el-form-item label="腾讯地图配置" prop="tencentMapKey">
                        <div class="w-120">
                            <div>
                                <el-input
                                    class="ls-input"
                                    v-model="formData.tencentMapKey"
                                    placeholder="请输入腾讯地图的配置Key"
                                />
                            </div>

                            <div class="form-tips">
                                如果没有Key，可前往：https://lbs.qq.com/ 申请
                                <br />
                                使用场景：1、PC端门店自提，获取地理定位；2、后台创建门店时需要获取地理位置
                            </div>
                        </div>
                    </el-form-item>
            </el-card>
        </el-form>

        <!--  表单功能键  -->
        <footer-btns v-perms="['setting:shop:setMapKey']">
            <el-button type="primary" @click="onSubmitFrom">保存</el-button>
        </footer-btns>
    </div>
</template>

<script lang="ts" setup name="settingMap">
    import { apiMapGet, apiMapSet } from "@/api/setting/map"
    import type { FormInstance } from "element-plus";
    import feedback from "@/utils/feedback";
    const formRef = ref<FormInstance>();
    // 表单数据
    const formData = reactive({
        tencentMapKey: ''
    });
    // 表单验证
    const rules = {
        tencentMapKey: [
            {
                required: true,
                message: '请输入腾讯地图的配置Key'
            }
        ]
    }


    const initFormData =  async() => {
        apiMapGet()
            .then(res => {
                // 表单同步于接口响应数据 
                formData.tencentMapKey = res.tencentMapKey
            })
            .catch(() => {
                feedback.msgError("数据加载失败，请刷新重载");
            })
    }

    // 提交表单
    const onSubmitFrom = async () => {
        await apiMapSet(formData);
        feedback.msgSuccess("操作成功");
    } 

    initFormData()
</script>

<style lang="scss" scoped>
.map-setting {
    padding-bottom: 60px;
}

.ls-card {
    .ls-input {
        width: 280px;
    }

    .card-title {
        font-size: 14px;
        font-weight: 500;
    }
}
</style>
