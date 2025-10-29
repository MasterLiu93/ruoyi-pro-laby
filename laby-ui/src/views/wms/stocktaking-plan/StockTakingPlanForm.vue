<!--
  盘点计划表单组件
  
  功能说明：
  1. 支持新增和编辑盘点计划
  2. 表单验证
  3. 成功后通知父组件刷新列表
  
  @author laby
  @date 2025-10-28
-->
<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px" v-loading="formLoading">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="计划名称" prop="planName">
            <el-input v-model="formData.planName" placeholder="请输入计划名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="仓库" prop="warehouseId">
            <el-select v-model="formData.warehouseId" placeholder="请选择仓库" class="!w-full">
              <el-option
                v-for="warehouse in warehouseList"
                :key="warehouse.id"
                :label="warehouse.warehouseName"
                :value="warehouse.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="盘点类型" prop="takingType">
            <el-select v-model="formData.takingType" placeholder="请选择盘点类型" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.WMS_STOCK_TAKING_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="范围类型" prop="scopeType">
            <el-select v-model="formData.scopeType" placeholder="请选择范围类型" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.WMS_STOCK_TAKING_SCOPE_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="计划开始时间" prop="planStartTime">
            <el-date-picker
              v-model="formData.planStartTime"
              type="datetime"
              placeholder="选择日期时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="计划结束时间" prop="planEndTime">
            <el-date-picker
              v-model="formData.planEndTime"
              type="datetime"
              placeholder="选择日期时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as StockTakingPlanApi from '@/api/wms/stockTakingPlan'
import * as WarehouseApi from '@/api/wms/warehouse'

defineOptions({ name: 'StockTakingPlanForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const warehouseList = ref([])

const formData = ref({
  id: undefined,
  planName: '',
  warehouseId: undefined,
  takingType: 1,
  scopeType: 1,
  planStartTime: '',
  planEndTime: '',
  remark: ''
})

const formRules = reactive({
  planName: [{ required: true, message: '计划名称不能为空', trigger: 'blur' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  takingType: [{ required: true, message: '请选择盘点类型', trigger: 'change' }],
  scopeType: [{ required: true, message: '请选择范围类型', trigger: 'change' }],
  planStartTime: [{ required: true, message: '请选择计划开始时间', trigger: 'change' }],
  planEndTime: [{ required: true, message: '请选择计划结束时间', trigger: 'change' }]
})
const formRef = ref()

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()

  if (id) {
    formLoading.value = true
    try {
      const data = await StockTakingPlanApi.getStockTakingPlan(id)
      // 格式化时间字段
      formData.value = {
        ...data,
        planStartTime: data.planStartTime ? formatDate(data.planStartTime, 'YYYY-MM-DD HH:mm:ss') : '',
        planEndTime: data.planEndTime ? formatDate(data.planEndTime, 'YYYY-MM-DD HH:mm:ss') : ''
      }
    } finally {
      formLoading.value = false
    }
  }

  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
}
defineExpose({ open })

const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = formData.value as unknown as StockTakingPlanApi.StockTakingPlanVO
    if (formType.value === 'create') {
      await StockTakingPlanApi.createStockTakingPlan(data)
      message.success(t('common.createSuccess'))
    } else {
      await StockTakingPlanApi.updateStockTakingPlan(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    id: undefined,
    planName: '',
    warehouseId: undefined,
    takingType: 1,
    scopeType: 1,
    planStartTime: '',
    planEndTime: '',
    remark: ''
  }
  formRef.value?.resetFields()
}
</script>

