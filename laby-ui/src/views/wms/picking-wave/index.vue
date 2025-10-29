<template>
  <ContentWrap>
    <!-- 搜索区域 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-form-item label="波次号" prop="waveNo">
        <el-input
          v-model="queryParams.waveNo"
          placeholder="请输入波次号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="仓库" prop="warehouseId">
        <el-select
          v-model="queryParams.warehouseId"
          placeholder="请选择仓库"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="warehouse in warehouseList"
            :key="warehouse.id"
            :label="warehouse.warehouseName"
            :value="warehouse.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="波次类型" prop="waveType">
        <el-select
          v-model="queryParams.waveType"
          placeholder="请选择波次类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_WAVE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="波次状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择波次状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_WAVE_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery" type="primary">
          <Icon icon="ep:search" class="mr-5px" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" />
          重置
        </el-button>
        <el-button @click="openForm('create')" type="success" v-hasPermi="['wms:picking-wave:create']">
          <Icon icon="ep:plus" class="mr-5px" />
          新建波次
        </el-button>
        <el-button @click="handleGenerate" type="warning" v-hasPermi="['wms:picking-wave:create']">
          <Icon icon="ep:magic-stick" class="mr-5px" />
          自动生成
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 列表区域 -->
    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="波次号" prop="waveNo" min-width="180" show-overflow-tooltip fixed />
      <el-table-column label="仓库" prop="warehouseName" min-width="120" show-overflow-tooltip />
      <el-table-column label="波次类型" prop="waveType" min-width="100" align="center">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.WMS_WAVE_TYPE" :value="row.waveType" />
        </template>
      </el-table-column>
      <el-table-column label="关联出库单" prop="outboundNos" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.outboundNos && row.outboundNos.length > 0">
            {{ row.outboundNos.join(', ') }}
          </span>
          <span v-else style="color: #999">-</span>
        </template>
      </el-table-column>
      <el-table-column label="出库单数" prop="orderCount" min-width="90" align="center" />
      <el-table-column label="总数量" prop="totalQuantity" min-width="90" align="center" />
      <el-table-column label="优先级" prop="priority" min-width="80" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.priority === 3" type="danger">紧急</el-tag>
          <el-tag v-else-if="row.priority === 2" type="warning">重要</el-tag>
          <el-tag v-else type="info">普通</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="拣货员" prop="pickerName" min-width="100" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.pickerName">{{ row.pickerName }}</span>
          <el-tag v-else type="info">未分配</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="波次状态" prop="status" min-width="100" align="center">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.WMS_WAVE_STATUS" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column
        label="开始时间"
        prop="startTime"
        min-width="160"
        :formatter="dateFormatter"
      />
      <el-table-column label="结束时间" prop="endTime" min-width="160" :formatter="dateFormatter" />
      <el-table-column label="备注" prop="remark" min-width="150" show-overflow-tooltip />
      <el-table-column
        label="创建时间"
        prop="createTime"
        min-width="160"
        :formatter="dateFormatter"
      />
      <el-table-column label="操作" align="center" min-width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            @click="openDetail(row.id)"
            v-hasPermi="['wms:picking-wave:query']"
          >
            详情
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('update', row.id)"
            v-hasPermi="['wms:picking-wave:update']"
            v-if="row.status === 1"
          >
            编辑
          </el-button>
          <el-button
            link
            type="primary"
            @click="handleAssign(row)"
            v-hasPermi="['wms:picking-wave:update']"
            v-if="row.status === 1"
          >
            分配拣货员
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleCancel(row.id)"
            v-hasPermi="['wms:picking-wave:update']"
            v-if="row.status !== 4 && row.status !== 5"
          >
            取消
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(row.id)"
            v-hasPermi="['wms:picking-wave:delete']"
            v-if="row.status === 1"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <Pagination
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      :total="total"
      @pagination="getList"
    />

    <!-- 表单弹窗 -->
    <PickingWaveForm ref="formRef" @success="getList" />

    <!-- 详情弹窗 -->
    <PickingWaveDetail ref="detailRef" />

    <!-- 分配拣货员弹窗 -->
    <el-dialog v-model="assignDialogVisible" title="分配拣货员" width="600px" append-to-body>
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="100px">
        <el-form-item label="波次号" prop="waveNo">
          <el-input v-model="assignForm.waveNo" disabled />
        </el-form-item>
        <el-form-item label="拣货员" prop="pickerId">
          <el-select v-model="assignForm.pickerId" placeholder="请选择拣货员" class="!w-full">
            <el-option
              v-for="user in pickerList"
              :key="user.id"
              :label="user.nickname"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign" :loading="assignLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 自动生成弹窗 -->
    <el-dialog v-model="generateDialogVisible" title="自动生成拣货波次" width="600px" append-to-body>
      <el-form
        ref="generateFormRef"
        :model="generateForm"
        :rules="generateRules"
        label-width="100px"
      >
        <el-form-item label="仓库" prop="warehouseId">
          <el-select v-model="generateForm.warehouseId" placeholder="请选择仓库" class="!w-full">
            <el-option
              v-for="warehouse in warehouseList"
              :key="warehouse.id"
              :label="warehouse.name"
              :value="warehouse.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="波次类型" prop="waveType">
          <el-select v-model="generateForm.waveType" placeholder="请选择波次类型" class="!w-full">
            <el-option
              v-for="dict in getIntDictOptions(DICT_TYPE.WMS_WAVE_TYPE)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-alert
          title="说明"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
        >
          <template #default>
            <div>系统将自动查询该仓库所有"已审核"状态的出库单，并根据波次类型生成拣货波次：</div>
            <ul style="margin-top: 10px; padding-left: 20px">
              <li>批次拣货：按优先级分组，每组最多20个出库单</li>
              <li>分区拣货：按库区分组，每组最多10个出库单</li>
              <li>单品拣货：按商品分组，每组最多5个出库单</li>
            </ul>
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGenerate" :loading="generateLoading">确定生成</el-button>
      </template>
    </el-dialog>
  </ContentWrap>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as PickingWaveApi from '@/api/wms/pickingWave'
import * as WarehouseApi from '@/api/wms/warehouse'
import * as UserApi from '@/api/system/user'
import PickingWaveForm from './PickingWaveForm.vue'
import PickingWaveDetail from './PickingWaveDetail.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

/** 列表数据 */
const loading = ref(true)
const list = ref([])
const total = ref(0)
const queryFormRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  waveNo: undefined,
  warehouseId: undefined,
  waveType: undefined,
  status: undefined,
  pickerId: undefined,
  createTime: []
})

/** 仓库列表 */
const warehouseList = ref([])

/** 拣货员列表 */
const pickerList = ref([])

/** 搜索 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 获取列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PickingWaveApi.getPickingWavePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取仓库列表 */
const getWarehouseList = async () => {
  const data = await WarehouseApi.getWarehouseSimpleList()
  warehouseList.value = data
}

/** 获取拣货员列表（查询所有用户，实际应根据角色筛选） */
const getPickerList = async () => {
  const data = await UserApi.getSimpleUserList()
  pickerList.value = data
}

/** 打开表单 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 打开详情 */
const detailRef = ref()
const openDetail = (id: number) => {
  detailRef.value.open(id)
}

/** 删除 */
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认删除该拣货波次吗？')
    await PickingWaveApi.deletePickingWave(id)
    ElMessage.success('删除成功')
    await getList()
  } catch {}
}

/** 取消 */
const handleCancel = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认取消该拣货波次吗？')
    await PickingWaveApi.cancelPickingWave(id)
    ElMessage.success('取消成功')
    await getList()
  } catch {}
}

/** 分配拣货员 */
const assignDialogVisible = ref(false)
const assignFormRef = ref()
const assignLoading = ref(false)
const assignForm = reactive({
  id: undefined,
  waveNo: '',
  pickerId: undefined
})
const assignRules = {
  pickerId: [{ required: true, message: '请选择拣货员', trigger: 'change' }]
}

const handleAssign = (row: any) => {
  assignForm.id = row.id
  assignForm.waveNo = row.waveNo
  assignForm.pickerId = row.pickerId
  assignDialogVisible.value = true
}

const submitAssign = async () => {
  await assignFormRef.value?.validate()
  assignLoading.value = true
  try {
    await PickingWaveApi.assignPickingWave(assignForm.id, assignForm.pickerId)
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    await getList()
  } finally {
    assignLoading.value = false
  }
}

/** 自动生成拣货波次 */
const generateDialogVisible = ref(false)
const generateFormRef = ref()
const generateLoading = ref(false)
const generateForm = reactive({
  warehouseId: undefined,
  waveType: 1
})
const generateRules = {
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  waveType: [{ required: true, message: '请选择波次类型', trigger: 'change' }]
}

const handleGenerate = () => {
  generateForm.warehouseId = queryParams.warehouseId
  generateDialogVisible.value = true
}

const submitGenerate = async () => {
  await generateFormRef.value?.validate()
  generateLoading.value = true
  try {
    const waveIds = await PickingWaveApi.generatePickingWaves(
      generateForm.warehouseId,
      generateForm.waveType
    )
    ElMessage.success(`成功生成 ${waveIds.length} 个拣货波次`)
    generateDialogVisible.value = false
    await getList()
  } finally {
    generateLoading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getList()
  getWarehouseList()
  getPickerList()
})
</script>

