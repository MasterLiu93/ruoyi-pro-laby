<!--
  移库单表单组件
  
  功能说明：
  1. 支持新增和编辑移库单
  2. 表单验证
  3. 成功后通知父组件刷新列表
  
  @author laby
  @date 2025-10-28
-->
<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="110px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="仓库" prop="warehouseId">
            <el-select
              v-model="formData.warehouseId"
              placeholder="请选择仓库"
              class="!w-full"
              @change="handleWarehouseChange"
            >
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
          <el-form-item label="移库类型" prop="moveType">
            <el-select v-model="formData.moveType" placeholder="请选择移库类型" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.WMS_STOCK_MOVE_TYPE)"
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

        <el-col :span="12">
          <el-form-item label="批次号" prop="batchNo">
            <el-input v-model="formData.batchNo" placeholder="请输入批次号（可选）" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="源库位" prop="fromLocationId">
            <el-select
              v-model="formData.fromLocationId"
              placeholder="请选择源库位"
              filterable
              class="!w-full"
              @change="handleFromLocationChange"
            >
              <el-option
                v-for="location in locationList"
                :key="location.id"
                :label="`${location.locationCode} (${location.locationName || ''})`"
                :value="location.id"
              />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="目标库位" prop="toLocationId">
            <el-select
              v-model="formData.toLocationId"
              placeholder="请选择目标库位"
              filterable
              class="!w-full"
              @change="handleToLocationChange"
            >
              <el-option
                v-for="location in locationList"
                :key="location.id"
                :label="`${location.locationCode} (${location.locationName || ''})`"
                :value="location.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="移库数量" prop="quantity">
            <el-input-number
              v-model="formData.quantity"
              :min="0.01"
              :precision="2"
              controls-position="right"
              class="!w-full"
            />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="移库原因" prop="moveReason">
            <el-input v-model="formData.moveReason" placeholder="请输入移库原因" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注"
        />
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
import * as StockMoveApi from '@/api/wms/stockMove'
import * as WarehouseApi from '@/api/wms/warehouse'
import * as WarehouseLocationApi from '@/api/wms/location'
import * as GoodsApi from '@/api/wms/goods'

defineOptions({ name: 'StockMoveForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const warehouseList = ref([])
const locationList = ref([])
const goodsList = ref([])

// 表单数据
const formData = ref({
  id: undefined,
  warehouseId: undefined,
  moveType: 1, // 默认库位调整
  goodsId: undefined,
  skuCode: '',
  goodsName: '',
  batchNo: '',
  fromLocationId: undefined,
  fromLocationCode: '',
  toLocationId: undefined,
  toLocationCode: '',
  quantity: 1,
  moveReason: '',
  remark: ''
})

// 表单验证规则
const formRules = reactive({
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  moveType: [{ required: true, message: '请选择移库类型', trigger: 'change' }],
  goodsId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  fromLocationId: [{ required: true, message: '请选择源库位', trigger: 'change' }],
  toLocationId: [{ required: true, message: '请选择目标库位', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入移库数量', trigger: 'blur' }]
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
      formData.value = await StockMoveApi.getStockMove(id)
    } finally {
      formLoading.value = false
    }
  }

  // 加载仓库列表
  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
  
  // 加载商品列表
  goodsList.value = await GoodsApi.getGoodsSimpleList()
}
defineExpose({ open })

/**
 * 仓库变更事件
 */
const handleWarehouseChange = async (warehouseId: number) => {
  // 清空库位选择
  formData.value.fromLocationId = undefined
  formData.value.fromLocationCode = ''
  formData.value.toLocationId = undefined
  formData.value.toLocationCode = ''
  
  // 加载该仓库的库位列表
  if (warehouseId) {
    const data = await WarehouseLocationApi.getWarehouseLocationSimpleList(warehouseId)
    locationList.value = data || []
  } else {
    locationList.value = []
  }
}

/**
 * 商品变更事件
 */
const handleGoodsChange = (goodsId: number) => {
  const goods = goodsList.value.find(item => item.id === goodsId)
  if (goods) {
    formData.value.skuCode = goods.skuCode
    formData.value.goodsName = goods.goodsName
  }
}

/**
 * 源库位变更事件
 */
const handleFromLocationChange = (locationId: number) => {
  const location = locationList.value.find(item => item.id === locationId)
  if (location) {
    formData.value.fromLocationCode = location.locationCode
  }
}

/**
 * 目标库位变更事件
 */
const handleToLocationChange = (locationId: number) => {
  const location = locationList.value.find(item => item.id === locationId)
  if (location) {
    formData.value.toLocationCode = location.locationCode
  }
}

/**
 * 提交表单
 */
const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  
  // 校验源库位和目标库位不能相同
  if (formData.value.fromLocationId === formData.value.toLocationId) {
    message.error('源库位和目标库位不能相同')
    return
  }
  
  formLoading.value = true
  try {
    const data = formData.value as unknown as StockMoveApi.StockMoveVO
    if (formType.value === 'create') {
      await StockMoveApi.createStockMove(data)
      message.success(t('common.createSuccess'))
    } else {
      await StockMoveApi.updateStockMove(data)
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
    warehouseId: undefined,
    moveType: 1,
    goodsId: undefined,
    skuCode: '',
    goodsName: '',
    batchNo: '',
    fromLocationId: undefined,
    fromLocationCode: '',
    toLocationId: undefined,
    toLocationCode: '',
    quantity: 1,
    moveReason: '',
    remark: ''
  }
  formRef.value?.resetFields()
}
</script>

