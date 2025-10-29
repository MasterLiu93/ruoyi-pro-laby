<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="1400px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="出库类型" prop="outboundType">
            <el-select v-model="formData.outboundType" placeholder="请选择出库类型" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.WMS_OUTBOUND_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
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
        <el-col :span="8">
          <el-form-item label="预计发货时间" prop="expectedShipmentTime">
            <el-date-picker
              v-model="formData.expectedShipmentTime"
              type="datetime"
              value-format="x"
              placeholder="选择预计发货时间"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="客户ID" prop="customerId">
            <el-input-number v-model="formData.customerId" placeholder="请输入客户ID" class="!w-full" :controls="false" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="客户名称" prop="customerName">
            <el-input v-model="formData.customerName" placeholder="请输入客户名称" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="备注" prop="remark">
            <el-input v-model="formData.remark" placeholder="请输入备注" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 明细表格 -->
      <el-divider content-position="left">
        <span class="text-gray-600">出库明细</span>
      </el-divider>
      <el-button type="primary" plain size="small" @click="handleAddItem" class="mb-10px">
        <Icon icon="ep:plus" class="mr-5px" />
        添加商品
      </el-button>
      <el-table :data="formData.items" border stripe max-height="350px">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column label="商品ID" prop="goodsId" width="100">
          <template #default="{ row }">
            <el-input-number v-model="row.goodsId" placeholder="商品ID" size="small" class="!w-full" :controls="false" />
          </template>
        </el-table-column>
        <el-table-column label="批次号" prop="batchNo" width="140">
          <template #default="{ row }">
            <el-input v-model="row.batchNo" placeholder="批次号" size="small" clearable />
          </template>
        </el-table-column>
        <el-table-column label="计划数量" prop="planQuantity" width="110">
          <template #default="{ row }">
            <el-input-number v-model="row.planQuantity" :min="0" :precision="2" size="small" class="!w-full" :controls="false" />
          </template>
        </el-table-column>
        <el-table-column label="单价" prop="price" width="110">
          <template #default="{ row }">
            <el-input-number v-model="row.price" :min="0" :precision="2" size="small" class="!w-full" :controls="false" />
          </template>
        </el-table-column>
        <el-table-column label="金额" prop="amount" width="110" align="right">
          <template #default="{ row }">
            <span class="text-red-500 font-semibold">{{ (row.planQuantity * row.price || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="120">
          <template #default="{ row }">
            <el-input v-model="row.remark" placeholder="备注" size="small" clearable />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60" align="center">
          <template #default="{ $index }">
            <el-button link type="danger" size="small" @click="handleDeleteItem($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="submitForm">确 定</el-button>
    </template>
  </Dialog>
</template>


<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { getOutbound, createOutbound, updateOutbound } from '@/api/wms/outbound'
import { getWarehouseSimpleList } from '@/api/wms/warehouse'

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  outboundType: undefined,
  warehouseId: undefined,
  customerId: undefined,
  customerName: undefined,
  expectedShipmentTime: undefined,
  remark: undefined,
  items: []
})
const formRules = reactive({
  outboundType: [{ required: true, message: '出库类型不能为空', trigger: 'change' }],
  warehouseId: [{ required: true, message: '仓库不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref
const warehouseList = ref([]) // 仓库列表

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新增出库单' : '修改出库单'
  formType.value = type
  resetForm()
  
  // 加载仓库列表
  await loadWarehouseList()
  
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const data = await getOutbound(id)
      formData.value = data
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as any
    if (formType.value === 'create') {
      await createOutbound(data)
      message.success('新增成功')
    } else {
      await updateOutbound(data)
      message.success('修改成功')
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    outboundType: undefined,
    warehouseId: undefined,
    customerId: undefined,
    customerName: undefined,
    expectedShipmentTime: undefined,
    remark: undefined,
    items: []
  }
  formRef.value?.resetFields()
}

/** 加载仓库列表 */
const loadWarehouseList = async () => {
  const data = await getWarehouseSimpleList()
  warehouseList.value = data
}

/** 添加商品明细 */
const handleAddItem = () => {
  formData.value.items.push({
    goodsId: undefined,
    batchNo: undefined,
    planQuantity: 0,
    pickedQuantity: 0,
    shippedQuantity: 0,
    price: 0,
    amount: 0,
    remark: undefined
  })
}

/** 删除商品明细 */
const handleDeleteItem = (index: number) => {
  formData.value.items.splice(index, 1)
}
</script>

