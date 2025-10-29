<!--
  盘点单表单组件
  
  功能说明：
  1. 支持新增和编辑盘点单
  2. 表单验证
  3. 成功后通知父组件刷新列表
  
  @author laby
  @date 2025-10-28
-->
<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px" v-loading="formLoading">
      <el-row :gutter="20">
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
        <el-col :span="12">
          <el-form-item label="商品" prop="goodsId">
            <el-select
              v-model="formData.goodsId"
              placeholder="请选择商品"
              filterable
              class="!w-full"
              @change="handleGoodsChange"
            >
              <el-option
                v-for="goods in goodsList"
                :key="goods.id"
                :label="`${goods.goodsName} (${goods.skuCode})`"
                :value="goods.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="账面数量" prop="bookQuantity">
            <el-input-number
              v-model="formData.bookQuantity"
              :min="0"
              :precision="2"
              controls-position="right"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="批次号" prop="batchNo">
            <el-input v-model="formData.batchNo" placeholder="请输入批次号（可选）" />
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
import * as StockTakingApi from '@/api/wms/stockTaking'
import * as WarehouseApi from '@/api/wms/warehouse'
import * as GoodsApi from '@/api/wms/goods'

defineOptions({ name: 'StockTakingForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const warehouseList = ref([])
const goodsList = ref([])

const formData = ref({
  id: undefined,
  warehouseId: undefined,
  goodsId: undefined,
  skuCode: '',
  goodsName: '',
  batchNo: '',
  bookQuantity: 0,
  remark: ''
})

const formRules = reactive({
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  goodsId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  bookQuantity: [{ required: true, message: '请输入账面数量', trigger: 'blur' }]
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
      formData.value = await StockTakingApi.getStockTaking(id)
    } finally {
      formLoading.value = false
    }
  }

  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
  goodsList.value = await GoodsApi.getGoodsSimpleList()
}
defineExpose({ open })

const handleGoodsChange = (goodsId: number) => {
  const goods = goodsList.value.find(item => item.id === goodsId)
  if (goods) {
    formData.value.skuCode = goods.skuCode
    formData.value.goodsName = goods.goodsName
  }
}

const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = formData.value as unknown as StockTakingApi.StockTakingVO
    if (formType.value === 'create') {
      await StockTakingApi.createStockTaking(data)
      message.success(t('common.createSuccess'))
    } else {
      await StockTakingApi.updateStockTaking(data)
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
    warehouseId: undefined,
    goodsId: undefined,
    skuCode: '',
    goodsName: '',
    batchNo: '',
    bookQuantity: 0,
    remark: ''
  }
  formRef.value?.resetFields()
}
</script>

