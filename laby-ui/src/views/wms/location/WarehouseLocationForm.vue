<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="仓库" prop="warehouseId">
        <el-select
          v-model="formData.warehouseId"
          placeholder="请选择仓库"
          clearable
          @change="handleWarehouseChange"
          class="!w-full"
        >
          <el-option
            v-for="warehouse in warehouseList"
            :key="warehouse.id"
            :label="warehouse.warehouseName"
            :value="warehouse.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="库区" prop="areaId">
        <el-select v-model="formData.areaId" placeholder="请选择库区" clearable class="!w-full">
          <el-option
            v-for="area in areaList"
            :key="area.id"
            :label="area.areaName"
            :value="area.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="库位编码" prop="locationCode">
        <el-input v-model="formData.locationCode" placeholder="请输入库位编码" />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="排号" prop="rowNo">
            <el-input-number v-model="formData.rowNo" :min="1" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="列号" prop="columnNo">
            <el-input-number v-model="formData.columnNo" :min="1" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="层号" prop="layerNo">
            <el-input-number v-model="formData.layerNo" :min="1" class="!w-full" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="库位类型" prop="locationType">
        <el-select
          v-model="formData.locationType"
          placeholder="请选择库位类型"
          clearable
          class="!w-full"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_WAREHOUSE_LOCATION_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="容量(㎡)" prop="capacity">
            <el-input-number v-model="formData.capacity" :min="0" :precision="2" placeholder="请输入容量" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最大承重(kg)" prop="maxWeight">
            <el-input-number
              v-model="formData.maxWeight"
              :min="0"
              :precision="2"
              placeholder="请输入最大承重"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" placeholder="请选择状态" class="!w-full">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_LOCATION_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" />
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
import * as WarehouseLocationApi from '@/api/wms/location'
import * as WarehouseApi from '@/api/wms/warehouse'
import * as WarehouseAreaApi from '@/api/wms/area'

/** 库位管理 表单 */
defineOptions({ name: 'WarehouseLocationForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  warehouseId: undefined,
  areaId: undefined,
  locationCode: undefined,
  rowNo: undefined,
  columnNo: undefined,
  layerNo: undefined,
  locationType: 1,
  capacity: undefined,
  maxWeight: undefined,
  status: 1,
  remark: undefined
})
const formRules = reactive({
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  areaId: [{ required: true, message: '请选择库区', trigger: 'change' }],
  locationCode: [{ required: true, message: '库位编码不能为空', trigger: 'blur' }],
  rowNo: [{ required: true, message: '请输入排号', trigger: 'blur' }],
  columnNo: [{ required: true, message: '请输入列号', trigger: 'blur' }],
  layerNo: [{ required: true, message: '请输入层号', trigger: 'blur' }],
  locationType: [{ required: true, message: '请选择库位类型', trigger: 'change' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }],
  maxWeight: [{ required: true, message: '请输入最大承重', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref
const warehouseList = ref([]) // 仓库列表
const areaList = ref([]) // 库区列表

/** 仓库切换 */
const handleWarehouseChange = async (warehouseId: number) => {
  formData.value.areaId = undefined
  if (warehouseId) {
    // 加载库区列表
    areaList.value = await WarehouseAreaApi.getWarehouseAreaListByWarehouseId(warehouseId)
  } else {
    areaList.value = []
  }
}

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await WarehouseLocationApi.getWarehouseLocation(id)
      // 加载库区列表
      if (formData.value.warehouseId) {
        areaList.value = await WarehouseAreaApi.getWarehouseAreaListByWarehouseId(formData.value.warehouseId)
      }
    } finally {
      formLoading.value = false
    }
  }
  // 加载仓库列表
  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as WarehouseLocationApi.WarehouseLocationVO
    if (formType.value === 'create') {
      await WarehouseLocationApi.createWarehouseLocation(data)
      message.success(t('common.createSuccess'))
    } else {
      await WarehouseLocationApi.updateWarehouseLocation(data)
      message.success(t('common.updateSuccess'))
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
    warehouseId: undefined,
    areaId: undefined,
    locationCode: undefined,
    rowNo: undefined,
    columnNo: undefined,
    layerNo: undefined,
    locationType: 1,
    capacity: undefined,
    maxWeight: undefined,
    status: 1,
    remark: undefined
  }
  areaList.value = []
  formRef.value?.resetFields()
}
</script>

