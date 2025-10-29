<!--
  承运商信息表单组件
  
  功能说明：
  1. 支持新增和编辑承运商
  2. 表单验证
  3. 成功后通知父组件刷新列表
  
  @author laby
  @date 2025-10-28
-->
<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="1000px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <!-- 基本信息 -->
        <el-col :span="24">
          <el-divider content-position="left">基本信息</el-divider>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="承运商编码" prop="carrierCode">
            <el-input
              v-model="formData.carrierCode"
              placeholder="请输入承运商编码，如：CARRIER-001"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="承运商名称" prop="carrierName">
            <el-input
              v-model="formData.carrierName"
              placeholder="请输入承运商名称，如：顺丰速运"
              maxlength="100"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="承运商类型" prop="carrierType">
            <el-select
              v-model="formData.carrierType"
              placeholder="请选择承运商类型"
              class="!w-full"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.WMS_CARRIER_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio
                v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
                :key="dict.value"
                :label="dict.value"
              >
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        
        <!-- 联系信息 -->
        <el-col :span="24">
          <el-divider content-position="left">联系信息</el-divider>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="联系人" prop="contactPerson">
            <el-input
              v-model="formData.contactPerson"
              placeholder="请输入联系人"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="联系电话" prop="contactPhone">
            <el-input
              v-model="formData.contactPhone"
              placeholder="请输入联系电话"
              maxlength="20"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="邮箱" prop="contactEmail">
            <el-input
              v-model="formData.contactEmail"
              placeholder="请输入邮箱"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
        
        <!-- 服务信息 -->
        <el-col :span="24">
          <el-divider content-position="left">服务信息</el-divider>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="服务区域" prop="serviceArea">
            <el-input
              v-model="formData.serviceArea"
              placeholder="如：全国范围"
              maxlength="500"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="时效要求" prop="timeLimit">
            <el-input
              v-model="formData.timeLimit"
              placeholder="如：同城24小时，跨省48小时"
              maxlength="100"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="24">
          <el-form-item label="收费标准" prop="priceStandard">
            <el-input
              type="textarea"
              v-model="formData.priceStandard"
              placeholder="请输入收费标准，如：首重1kg/15元，续重1kg/5元"
              :rows="2"
              maxlength="500"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="服务评分" prop="rating">
            <el-rate
              v-model="formData.rating"
              show-score
              text-color="#ff9900"
              score-template="{value} 分"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="合作开始日期" prop="cooperationStartDate">
            <el-date-picker
              v-model="formData.cooperationStartDate"
              type="date"
              placeholder="选择合作开始日期"
              value-format="YYYY-MM-DD"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input
              type="textarea"
              v-model="formData.remark"
              placeholder="请输入备注信息"
              :rows="3"
              maxlength="500"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as CarrierApi from '@/api/wms/carrier'

defineOptions({ name: 'CarrierForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')

// 表单数据
const formData = ref({
  id: undefined,
  carrierCode: undefined,
  carrierName: undefined,
  carrierType: 1, // 默认值：快递
  contactPerson: undefined,
  contactPhone: undefined,
  contactEmail: undefined,
  serviceArea: undefined,
  priceStandard: undefined,
  timeLimit: undefined,
  rating: 5.0, // 默认值：5.0
  cooperationStartDate: undefined,
  status: 1, // 默认值：启用
  remark: undefined
})

// 表单验证规则
const formRules = reactive({
  carrierCode: [{ required: true, message: '承运商编码不能为空', trigger: 'blur' }],
  carrierName: [{ required: true, message: '承运商名称不能为空', trigger: 'blur' }],
  carrierType: [{ required: true, message: '请选择承运商类型', trigger: 'change' }],
  contactPhone: [
    { required: true, message: '联系电话不能为空', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$|^\d{3,4}-\d{7,8}$/, message: '请输入正确的电话号码', trigger: 'blur' }
  ],
  contactEmail: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})
const formRef = ref()

/**
 * 打开弹窗
 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  
  // 修改时，加载数据
  if (id) {
    formLoading.value = true
    try {
      const data = await CarrierApi.getCarrier(id)
      formData.value = data
      
      // 处理合作日期格式（LocalDate 数组 → 字符串）
      if (data.cooperationStartDate && Array.isArray(data.cooperationStartDate)) {
        const [year, month, day] = data.cooperationStartDate
        formData.value.cooperationStartDate = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
      }
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

/**
 * 提交表单
 */
const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = formData.value as unknown as CarrierApi.CarrierVO
    if (formType.value === 'create') {
      await CarrierApi.createCarrier(data)
      message.success(t('common.createSuccess'))
    } else {
      await CarrierApi.updateCarrier(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/**
 * 重置表单
 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    carrierCode: undefined,
    carrierName: undefined,
    carrierType: 1,
    contactPerson: undefined,
    contactPhone: undefined,
    contactEmail: undefined,
    serviceArea: undefined,
    priceStandard: undefined,
    timeLimit: undefined,
    rating: 5.0,
    cooperationStartDate: undefined,
    status: 1,
    remark: undefined
  }
  formRef.value?.resetFields()
}
</script>

