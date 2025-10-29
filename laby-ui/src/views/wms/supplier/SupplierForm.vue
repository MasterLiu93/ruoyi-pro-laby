<!--
  供应商信息表单组件
  
  功能说明：
  1. 支持新增和编辑供应商
  2. 表单验证
  3. 成功后通知父组件刷新列表
  
  @author laby
  @date 2025-10-28
-->
<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="1200px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="formLoading"
    >
      <el-divider content-position="left">基本信息</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="供应商编码" prop="supplierCode">
            <el-input v-model="formData.supplierCode" placeholder="请输入供应商编码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="供应商名称" prop="supplierName">
            <el-input v-model="formData.supplierName" placeholder="请输入供应商名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="供应商类型" prop="supplierType">
            <el-select v-model="formData.supplierType" placeholder="请选择供应商类型" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.WMS_SUPPLIER_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="信用等级" prop="creditLevel">
            <el-select v-model="formData.creditLevel" placeholder="请选择信用等级" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.WMS_SUPPLIER_CREDIT_LEVEL)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="合作开始日期" prop="cooperationStartDate">
            <el-date-picker
              v-model="formData.cooperationStartDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择合作开始日期"
              class="!w-full"
            />
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
      </el-row>

      <el-divider content-position="left">联系信息</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="联系人" prop="contactPerson">
            <el-input v-model="formData.contactPerson" placeholder="请输入联系人" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="联系电话" prop="contactPhone">
            <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="邮箱" prop="contactEmail">
            <el-input v-model="formData.contactEmail" placeholder="请输入邮箱" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider content-position="left">地址信息</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="省份" prop="province">
            <el-input v-model="formData.province" placeholder="请输入省份" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="城市" prop="city">
            <el-input v-model="formData.city" placeholder="请输入城市" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="区县" prop="district">
            <el-input v-model="formData.district" placeholder="请输入区县" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="详细地址" prop="address">
            <el-input v-model="formData.address" placeholder="请输入详细地址" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider content-position="left">其他信息</el-divider>
      <el-row>
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="formData.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注"
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
import * as SupplierApi from '@/api/wms/supplier'

/**
 * 供应商表单组件定义
 */
defineOptions({ name: 'SupplierForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')

// 表单数据
const formData = ref({
  id: undefined,
  supplierCode: undefined,
  supplierName: undefined,
  supplierType: 1, // 默认值：普通供应商
  contactPerson: undefined,
  contactPhone: undefined,
  contactEmail: undefined,
  province: undefined,
  city: undefined,
  district: undefined,
  address: undefined,
  creditLevel: 2, // 默认值：良好
  cooperationStartDate: undefined,
  status: 1, // 默认值：启用
  remark: undefined
})

// 表单验证规则
const formRules = reactive({
  supplierCode: [{ required: true, message: '供应商编码不能为空', trigger: 'blur' }],
  supplierName: [{ required: true, message: '供应商名称不能为空', trigger: 'blur' }],
  supplierType: [{ required: true, message: '请选择供应商类型', trigger: 'change' }],
  contactPhone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  contactEmail: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
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
      const data = await SupplierApi.getSupplier(id)
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
    const data = formData.value as unknown as SupplierApi.SupplierVO
    if (formType.value === 'create') {
      await SupplierApi.createSupplier(data)
      message.success(t('common.createSuccess'))
    } else {
      await SupplierApi.updateSupplier(data)
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
    supplierCode: undefined,
    supplierName: undefined,
    supplierType: 1,
    contactPerson: undefined,
    contactPhone: undefined,
    contactEmail: undefined,
    province: undefined,
    city: undefined,
    district: undefined,
    address: undefined,
    creditLevel: 2,
    cooperationStartDate: undefined,
    status: 1,
    remark: undefined
  }
  formRef.value?.resetFields()
}
</script>

