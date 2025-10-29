<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="1400px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="仓库" prop="warehouseId">
            <el-select
              v-model="formData.warehouseId"
              placeholder="请选择仓库"
              class="!w-full"
              :disabled="formType === 'update'"
            >
              <el-option
                v-for="warehouse in warehouseList"
                :key="warehouse.id"
                :label="warehouse.name"
                :value="warehouse.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="波次类型" prop="waveType">
            <el-select v-model="formData.waveType" placeholder="请选择波次类型" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.WMS_WAVE_TYPE)"
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
          <el-form-item label="优先级" prop="priority">
            <el-select v-model="formData.priority" placeholder="请选择优先级" class="!w-full">
              <el-option label="普通" :value="1" />
              <el-option label="重要" :value="2" />
              <el-option label="紧急" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="拣货员" prop="pickerId">
            <el-select v-model="formData.pickerId" placeholder="请选择拣货员" clearable class="!w-full">
              <el-option
                v-for="user in pickerList"
                :key="user.id"
                :label="user.nickname"
                :value="user.id"
              />
            </el-select>
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

      <el-divider content-position="left">
        <span style="font-weight: bold">出库单列表</span>
      </el-divider>

      <el-form-item label="" prop="outboundIds">
        <el-button @click="openOutboundSelector" type="primary" :disabled="formType === 'update'">
          <Icon icon="ep:plus" class="mr-5px" />
          添加出库单
        </el-button>
        <el-table :data="selectedOutbounds" border style="margin-top: 10px" max-height="350px">
          <el-table-column label="出库单号" prop="outboundNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="出库类型" prop="outboundType" min-width="100" align="center">
            <template #default="{ row }">
              <dict-tag :type="DICT_TYPE.WMS_OUTBOUND_TYPE" :value="row.outboundType" />
            </template>
          </el-table-column>
          <el-table-column label="客户" prop="customer" min-width="120" show-overflow-tooltip />
          <el-table-column label="总数量" prop="totalQuantity" min-width="90" align="center" />
          <el-table-column label="总金额" prop="totalAmount" min-width="120" align="right">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: bold">
                {{ row.totalAmount ? `¥${row.totalAmount.toFixed(2)}` : '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column
            label="预计发货时间"
            prop="expectedShipmentTime"
            min-width="160"
            :formatter="dateFormatter"
          />
          <el-table-column label="操作" align="center" width="80" fixed="right">
            <template #default="{ row }">
              <el-button
                link
                type="danger"
                @click="handleRemoveOutbound(row.id)"
                :disabled="formType === 'update'"
              >
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitForm" :loading="formLoading">确定</el-button>
    </template>

    <!-- 出库单选择器 -->
    <el-dialog
      v-model="outboundSelectorVisible"
      title="选择出库单"
      width="1200px"
      append-to-body
    >
      <el-form :inline="true" :model="outboundQueryParams" class="-mb-15px" label-width="auto">
        <el-form-item label="出库单号">
          <el-input
            v-model="outboundQueryParams.outboundNo"
            placeholder="请输入出库单号"
            clearable
            class="!w-240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button @click="getOutboundList" type="primary">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table
        ref="outboundTableRef"
        :data="outboundList"
        border
        @selection-change="handleOutboundSelectionChange"
        max-height="400px"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="出库单号" prop="outboundNo" min-width="180" show-overflow-tooltip />
        <el-table-column label="出库类型" prop="outboundType" min-width="100" align="center">
          <template #default="{ row }">
            <dict-tag :type="DICT_TYPE.WMS_OUTBOUND_TYPE" :value="row.outboundType" />
          </template>
        </el-table-column>
        <el-table-column label="客户" prop="customer" min-width="120" show-overflow-tooltip />
        <el-table-column label="总数量" prop="totalQuantity" min-width="90" align="center" />
        <el-table-column
          label="预计发货时间"
          prop="expectedShipmentTime"
          min-width="160"
          :formatter="dateFormatter"
        />
      </el-table>

      <Pagination
        v-model:page="outboundQueryParams.pageNo"
        v-model:limit="outboundQueryParams.pageSize"
        :total="outboundTotal"
        @pagination="getOutboundList"
      />

      <template #footer>
        <el-button @click="outboundSelectorVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmOutboundSelection">确定</el-button>
      </template>
    </el-dialog>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as PickingWaveApi from '@/api/wms/pickingWave'
import * as WarehouseApi from '@/api/wms/warehouse'
import * as OutboundApi from '@/api/wms/outbound'
import * as UserApi from '@/api/system/user'
import { ElMessage } from 'element-plus'

/** 定义 emit */
const emit = defineEmits(['success'])

/** 对话框 */
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')

/** 表单 */
const formRef = ref()
const formData = ref({
  id: undefined,
  warehouseId: undefined,
  waveType: 'BATCH',
  priority: 1,
  pickerId: undefined,
  remark: '',
  outboundIds: []
})
const formRules = reactive({
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  waveType: [{ required: true, message: '请选择波次类型', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  outboundIds: [
    {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!selectedOutbounds.value || selectedOutbounds.value.length === 0) {
          callback(new Error('请至少添加一个出库单'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
})

/** 仓库列表 */
const warehouseList = ref([])

/** 拣货员列表 */
const pickerList = ref([])

/** 已选择的出库单 */
const selectedOutbounds = ref([])

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '新建拣货波次' : '修改拣货波次'
  formType.value = type
  resetForm()

  // 加载仓库列表
  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
  // 加载拣货员列表
  pickerList.value = await UserApi.getSimpleUserList()

  if (id) {
    formLoading.value = true
    try {
      const data = await PickingWaveApi.getPickingWave(id)
      formData.value = data
      // 加载出库单详情
      if (data.outboundIds && data.outboundIds.length > 0) {
        // TODO: 批量查询出库单详情
        // 简化实现：逐个查询
        selectedOutbounds.value = []
        for (const outboundId of data.outboundIds) {
          const outbound = await OutboundApi.getOutbound(outboundId)
          selectedOutbounds.value.push(outbound)
        }
      }
    } finally {
      formLoading.value = false
    }
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    warehouseId: undefined,
    waveType: 'BATCH',
    priority: 1,
    pickerId: undefined,
    remark: '',
    outboundIds: []
  }
  selectedOutbounds.value = []
  formRef.value?.resetFields()
}

/** 提交表单 */
const submitForm = async () => {
  await formRef.value?.validate()
  formLoading.value = true
  try {
    // 收集出库单ID
    formData.value.outboundIds = selectedOutbounds.value.map((item) => item.id)

    if (formType.value === 'create') {
      await PickingWaveApi.createPickingWave(formData.value)
      ElMessage.success('新建成功')
    } else {
      await PickingWaveApi.updatePickingWave(formData.value)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 出库单选择器 */
const outboundSelectorVisible = ref(false)
const outboundTableRef = ref()
const outboundList = ref([])
const outboundTotal = ref(0)
const outboundQueryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  outboundNo: undefined,
  status: 2 // 只查询"已审核"状态的出库单
})
const outboundSelection = ref([])

const openOutboundSelector = () => {
  outboundSelectorVisible.value = true
  getOutboundList()
}

const getOutboundList = async () => {
  const data = await OutboundApi.getOutboundPage(outboundQueryParams)
  outboundList.value = data.list
  outboundTotal.value = data.total
}

const handleOutboundSelectionChange = (selection: any[]) => {
  outboundSelection.value = selection
}

const confirmOutboundSelection = () => {
  if (outboundSelection.value.length === 0) {
    ElMessage.warning('请至少选择一个出库单')
    return
  }

  // 合并已选择的出库单（去重）
  outboundSelection.value.forEach((item) => {
    const exists = selectedOutbounds.value.some((outbound) => outbound.id === item.id)
    if (!exists) {
      selectedOutbounds.value.push(item)
    }
  })

  outboundSelectorVisible.value = false
  outboundSelection.value = []
}

const handleRemoveOutbound = (outboundId: number) => {
  selectedOutbounds.value = selectedOutbounds.value.filter((item) => item.id !== outboundId)
}

/** 暴露方法 */
defineExpose({ open })
</script>

