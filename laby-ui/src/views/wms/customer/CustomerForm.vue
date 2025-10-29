<!--
  客户信息表单组件
  
  功能说明：
  1. 支持新增和编辑客户
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
          <el-form-item label="客户编码" prop="customerCode">
            <el-input v-model="formData.customerCode" placeholder="请输入客户编码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户名称" prop="customerName">
            <el-input v-model="formData.customerName" placeholder="请输入客户名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户类型" prop="customerType">
            <el-select v-model="formData.customerType" placeholder="请选择客户类型" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.WMS_CUSTOMER_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户等级" prop="customerLevel">
            <el-select v-model="formData.customerLevel" placeholder="请选择客户等级" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.WMS_CUSTOMER_LEVEL)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="信用额度" prop="creditLimit">
            <el-input-number
              v-model="formData.creditLimit"
              :precision="2"
              :min="0"
              :step="1000"
              placeholder="请输入信用额度"
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

      <el-divider content-position="left">收货地址</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="省份" prop="deliveryProvince">
            <el-input v-model="formData.deliveryProvince" placeholder="请输入省份" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="城市" prop="deliveryCity">
            <el-input v-model="formData.deliveryCity" placeholder="请输入城市" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="区县" prop="deliveryDistrict">
            <el-input v-model="formData.deliveryDistrict" placeholder="请输入区县" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="详细地址" prop="deliveryAddress">
            <el-input v-model="formData.deliveryAddress" placeholder="请输入详细地址" />
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
import * as CustomerApi from '@/api/wms/customer'

/**
 * 客户表单组件定义
 */
defineOptions({ name: 'CustomerForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')

// 表单数据
const formData = ref({
  id: undefined,
  customerCode: undefined,
  customerName: undefined,
  customerType: 1, // 默认值：零售客户
  customerLevel: 4, // 默认值：普通客户
  contactPerson: undefined,
  contactPhone: undefined,
  contactEmail: undefined,
  deliveryProvince: undefined,
  deliveryCity: undefined,
  deliveryDistrict: undefined,
  deliveryAddress: undefined,
  creditLimit: undefined,
  status: 1, // 默认值：启用
  remark: undefined
})

// 表单验证规则
const formRules = reactive({
  customerCode: [{ required: true, message: '客户编码不能为空', trigger: 'blur' }],
  customerName: [{ required: true, message: '客户名称不能为空', trigger: 'blur' }],
  customerType: [{ required: true, message: '请选择客户类型', trigger: 'change' }],
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
      formData.value = await CustomerApi.getCustomer(id)
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
    const data = formData.value as unknown as CustomerApi.CustomerVO
    if (formType.value === 'create') {
      await CustomerApi.createCustomer(data)
      message.success(t('common.createSuccess'))
    } else {
      await CustomerApi.updateCustomer(data)
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
    customerCode: undefined,
    customerName: undefined,
    customerType: 1,
    customerLevel: 4,
    contactPerson: undefined,
    contactPhone: undefined,
    contactEmail: undefined,
    deliveryProvince: undefined,
    deliveryCity: undefined,
    deliveryDistrict: undefined,
    deliveryAddress: undefined,
    creditLimit: undefined,
    status: 1,
    remark: undefined
  }
  formRef.value?.resetFields()
}
</script>

