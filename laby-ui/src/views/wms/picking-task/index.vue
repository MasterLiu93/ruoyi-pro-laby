<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="88px"
    >
      <el-form-item label="任务编号" prop="taskNo">
        <el-input
          v-model="queryParams.taskNo"
          class="!w-240px"
          clearable
          placeholder="请输入任务编号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="出库单号" prop="outboundNo">
        <el-input
          v-model="queryParams.outboundNo"
          class="!w-240px"
          clearable
          placeholder="请输入出库单号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="queryParams.warehouseId" class="!w-240px" clearable placeholder="请选择仓库">
          <el-option
            v-for="warehouse in warehouseList"
            :key="warehouse.id"
            :label="warehouse.warehouseName"
            :value="warehouse.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="拣货员" prop="pickerId">
        <el-select v-model="queryParams.pickerId" class="!w-240px" clearable placeholder="请选择拣货员">
          <el-option
            v-for="user in pickerList"
            :key="user.id"
            :label="user.nickname"
            :value="user.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="任务状态" prop="status">
        <el-select v-model="queryParams.status" class="!w-240px" clearable placeholder="请选择状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_PICKING_TASK_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
          end-placeholder="结束日期"
          start-placeholder="开始日期"
          type="daterange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" />
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column align="center" label="任务编号" min-width="160" prop="taskNo" show-overflow-tooltip />
      <el-table-column align="center" label="出库单号" min-width="160" prop="outboundNo" show-overflow-tooltip />
      <el-table-column align="center" label="商品信息" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          <div>{{ row.goodsName }}</div>
          <div class="text-xs text-gray-500">SKU: {{ row.skuCode }}</div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="库位" min-width="120" prop="locationCode" show-overflow-tooltip />
      <el-table-column align="center" label="批次号" min-width="140" prop="batchNo" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.batchNo || '-' }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="计划数量" min-width="100" prop="planQuantity">
        <template #default="{ row }">
          <span class="font-medium">{{ row.planQuantity }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="实际数量" min-width="100" prop="actualQuantity">
        <template #default="{ row }">
          <span v-if="row.actualQuantity" class="text-primary font-medium">{{ row.actualQuantity }}</span>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="拣货员" min-width="100" prop="pickerName">
        <template #default="{ row }">
          {{ row.pickerName || '-' }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="任务状态" min-width="100" prop="status">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.WMS_PICKING_TASK_STATUS" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="拣货时间"
        min-width="155"
        prop="pickingTime"
      >
        <template #default="{ row }">
          {{ row.pickingTime ? dateFormatter(row.pickingTime) : '-' }}
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        min-width="155"
        prop="createTime"
      />
      <el-table-column align="center" fixed="right" label="操作" min-width="180">
        <template #default="{ row }">
          <el-button
            v-hasPermi="['wms:picking-task:query']"
            link
            type="primary"
            @click="handleDetail(row)"
          >
            详情
          </el-button>
          <el-button
            v-if="row.status === 1"
            v-hasPermi="['wms:picking-task:execute']"
            link
            type="primary"
            @click="handleAssign(row)"
          >
            分配
          </el-button>
          <el-button
            v-if="row.status === 1 || row.status === 2"
            v-hasPermi="['wms:picking-task:execute']"
            link
            type="success"
            @click="handlePick(row)"
          >
            拣货
          </el-button>
          <el-button
            v-if="row.status === 4"
            v-hasPermi="['wms:picking-task:exception']"
            link
            type="warning"
            @click="handleException(row)"
          >
            查看异常
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 任务详情弹窗 -->
  <el-dialog v-model="detailVisible" title="拣货任务详情" width="800px">
    <el-descriptions v-if="currentTask" :column="2" border>
      <el-descriptions-item label="任务编号">{{ currentTask.taskNo }}</el-descriptions-item>
      <el-descriptions-item label="出库单号">{{ currentTask.outboundNo }}</el-descriptions-item>
      <el-descriptions-item label="仓库">{{ currentTask.warehouseName }}</el-descriptions-item>
      <el-descriptions-item label="库位">{{ currentTask.locationCode }}</el-descriptions-item>
      <el-descriptions-item label="商品名称">{{ currentTask.goodsName }}</el-descriptions-item>
      <el-descriptions-item label="SKU编码">{{ currentTask.skuCode }}</el-descriptions-item>
      <el-descriptions-item label="批次号">{{ currentTask.batchNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="拣货顺序">{{ currentTask.sortOrder || '-' }}</el-descriptions-item>
      <el-descriptions-item label="计划数量">
        <span class="font-medium">{{ currentTask.planQuantity }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="实际数量">
        <span v-if="currentTask.actualQuantity" class="text-primary font-medium">
          {{ currentTask.actualQuantity }}
        </span>
        <span v-else class="text-gray-400">-</span>
      </el-descriptions-item>
      <el-descriptions-item label="拣货员">{{ currentTask.pickerName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="拣货时间">
        {{ currentTask.pickingTime ? dateFormatter(currentTask.pickingTime) : '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="任务状态">
        <dict-tag :type="DICT_TYPE.WMS_PICKING_TASK_STATUS" :value="currentTask.status" />
      </el-descriptions-item>
      <el-descriptions-item v-if="currentTask.exceptionType" label="异常类型">
        <dict-tag :type="DICT_TYPE.WMS_PICKING_EXCEPTION_TYPE" :value="currentTask.exceptionType" />
      </el-descriptions-item>
      <el-descriptions-item v-if="currentTask.exceptionRemark" label="异常说明" :span="2">
        {{ currentTask.exceptionRemark }}
      </el-descriptions-item>
      <el-descriptions-item v-if="currentTask.remark" label="备注" :span="2">
        {{ currentTask.remark }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ dateFormatter(currentTask.createTime) }}
      </el-descriptions-item>
    </el-descriptions>
  </el-dialog>

  <!-- 分配拣货员弹窗 -->
  <el-dialog v-model="assignVisible" title="分配拣货员" width="500px">
    <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="100px">
      <el-form-item label="拣货员" prop="pickerId">
        <el-select v-model="assignForm.pickerId" class="w-full" placeholder="请选择拣货员">
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
      <el-button @click="assignVisible = false">取消</el-button>
      <el-button type="primary" @click="submitAssign">确定</el-button>
    </template>
  </el-dialog>

  <!-- 拣货操作弹窗 -->
  <el-dialog v-model="pickVisible" title="执行拣货" width="600px">
    <el-form ref="pickFormRef" :model="pickForm" :rules="pickRules" label-width="120px">
      <el-form-item label="商品信息">
        <div>{{ currentTask?.goodsName }}</div>
        <div class="text-xs text-gray-500">SKU: {{ currentTask?.skuCode }}</div>
      </el-form-item>
      <el-form-item label="库位">
        {{ currentTask?.locationCode }}
      </el-form-item>
      <el-form-item label="计划拣货数量">
        <span class="font-medium text-lg">{{ currentTask?.planQuantity }}</span>
      </el-form-item>
      <el-form-item label="实际拣货数量" prop="actualQuantity">
        <el-input-number
          v-model="pickForm.actualQuantity"
          :max="currentTask?.planQuantity"
          :min="0"
          :precision="2"
          :step="1"
          class="w-full"
          controls-position="right"
        />
      </el-form-item>
      <el-form-item label="是否有异常">
        <el-switch v-model="hasException" />
      </el-form-item>
      <template v-if="hasException">
        <el-form-item label="异常类型" prop="exceptionType">
          <el-select v-model="pickForm.exceptionType" class="w-full" placeholder="请选择异常类型">
            <el-option
              v-for="dict in getDictOptions(DICT_TYPE.WMS_PICKING_EXCEPTION_TYPE)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="异常说明" prop="exceptionRemark">
          <el-input
            v-model="pickForm.exceptionRemark"
            :rows="3"
            placeholder="请输入异常说明"
            type="textarea"
          />
        </el-form-item>
      </template>
      <el-form-item label="备注">
        <el-input
          v-model="pickForm.remark"
          :rows="2"
          placeholder="请输入备注（可选）"
          type="textarea"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="pickVisible = false">取消</el-button>
      <el-button type="primary" @click="submitPick">确定拣货</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import { DICT_TYPE, getIntDictOptions, getDictOptions } from '@/utils/dict'
import * as PickingTaskApi from '@/api/wms/pickingTask'
import * as WarehouseApi from '@/api/wms/warehouse'
import * as UserApi from '@/api/system/user'

defineOptions({ name: 'WmsPickingTask' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  taskNo: undefined,
  outboundNo: undefined,
  warehouseId: undefined,
  pickerId: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref()

// 仓库列表
const warehouseList = ref([])
// 拣货员列表
const pickerList = ref([])

// 详情弹窗
const detailVisible = ref(false)
const currentTask = ref(null)

// 分配弹窗
const assignVisible = ref(false)
const assignForm = reactive({
  pickerId: undefined
})
const assignFormRef = ref()
const assignRules = {
  pickerId: [{ required: true, message: '请选择拣货员', trigger: 'change' }]
}

// 拣货弹窗
const pickVisible = ref(false)
const pickForm = reactive({
  id: undefined,
  actualQuantity: undefined,
  exceptionType: undefined,
  exceptionRemark: undefined,
  remark: undefined
})
const pickFormRef = ref()
const hasException = ref(false)
const pickRules = {
  actualQuantity: [{ required: true, message: '请输入实际拣货数量', trigger: 'blur' }],
  exceptionType: [
    { required: true, message: '请选择异常类型', trigger: 'change' },
    { validator: (rule, value, callback) => {
      if (hasException.value && !value) {
        callback(new Error('请选择异常类型'))
      } else {
        callback()
      }
    }, trigger: 'change' }
  ]
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PickingTaskApi.getPickingTaskPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 查看详情 */
const handleDetail = (row) => {
  currentTask.value = row
  detailVisible.value = true
}

/** 分配拣货员 */
const handleAssign = (row) => {
  currentTask.value = row
  assignForm.pickerId = row.pickerId
  assignVisible.value = true
}

/** 提交分配 */
const submitAssign = async () => {
  await assignFormRef.value.validate()
  const picker = pickerList.value.find(u => u.id === assignForm.pickerId)
  await PickingTaskApi.assignPickingTasks([currentTask.value.id], assignForm.pickerId, picker.nickname)
  message.success('分配成功')
  assignVisible.value = false
  getList()
}

/** 执行拣货 */
const handlePick = (row) => {
  currentTask.value = row
  pickForm.id = row.id
  pickForm.actualQuantity = row.planQuantity
  pickForm.exceptionType = undefined
  pickForm.exceptionRemark = undefined
  pickForm.remark = undefined
  hasException.value = false
  pickVisible.value = true
}

/** 提交拣货 */
const submitPick = async () => {
  await pickFormRef.value.validate()
  if (!hasException.value) {
    pickForm.exceptionType = undefined
    pickForm.exceptionRemark = undefined
  }
  await PickingTaskApi.executePicking(pickForm)
  message.success('拣货成功')
  pickVisible.value = false
  getList()
}

/** 查看异常 */
const handleException = (row) => {
  currentTask.value = row
  detailVisible.value = true
}

/** 初始化 */
onMounted(async () => {
  // 加载仓库列表
  const warehouseData = await WarehouseApi.getWarehouseSimpleList()
  warehouseList.value = warehouseData
  
  // 加载拣货员列表（假设有专门的拣货员角色）
  const userData = await UserApi.getSimpleUserList()
  pickerList.value = userData
  
  // 加载列表
  getList()
})
</script>

