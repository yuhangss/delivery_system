<template>
  <div style="width: 100%">
    <!--    操作区域-->
    <div style="margin: 10px 5px">
      <el-popconfirm title="确定删除这些数据?" @confirm="delBatch()">
        <template #reference>
          <el-button  type="danger">批量删除</el-button>
        </template>
      </el-popconfirm>
      <el-button type="primary" @click="exp" class="ml-5">导出</el-button>
      <el-upload
        action="http://localhost:9090/user/import"
        style="display:inline-block; margin:auto 10px"
        :show-file-list="false"
        accept=".xlsx"
        :on-success="handleExcelImportSuccess"
      >   
        <el-button type="primary" class="ml-5" color="green">导入</el-button>
      </el-upload>

    </div>
    <!--    搜索区域-->
    <div style="margin: 10px 5px">
      <el-input v-model="id" clearable type="text" placeholder="请输入订单号" style="width: 200px"></el-input>
      <el-input v-model="sender" clearable type="text" placeholder="请输入发件人姓名" style="width: 200px"></el-input>
      <el-button @click="load" type="primary" style="margin: 0 5px">
        搜索
      </el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>
    <el-table v-model:data="tableData"
              stripe
              border
              style="width: 100%"
              @selection-change="handleSelectionChange">

      <el-table-column type="selection" width="30">

      </el-table-column>
      <el-table-column prop="id" label="订单号"/>
      <el-table-column prop="sender" label="发件人"/>
      <!-- <el-table-column prop="senderid" label="发件人id"/> -->
      <el-table-column prop="receiver" label="收件人"/>
      <!-- <el-table-column prop="receiverid" label="收件人id"></el-table-column> -->
      <el-table-column prop="src" label="取货地址"></el-table-column>
      <el-table-column prop="dest" label="送货地址"></el-table-column>
      <el-table-column label="订单状态">
        <template #default="scope">
          <p v-if="scope.row.state === '000'">{{ this.state.state1 }}</p>
          <p v-if="scope.row.state === '100'">{{ this.state.state2 }}</p>
          <p v-if="scope.row.state === '110'">{{ this.state.state3 }}</p>
          <p v-if="scope.row.state === '120'">{{ this.state.state4 }}</p>
          <p v-if="scope.row.state === '121'">{{ this.state.state5 }}</p>
          <p v-if="scope.row.state === '122'">{{ this.state.state6 }}</p>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="mini" @click="OrderInfo(scope.row)">详情</el-button>
          <el-popconfirm title="确定删除该数据?" @confirm="deleteOrder(scope.row.id)">
            <template #reference>
              <el-button size="mini" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div class="demo-pagination-block">
      <el-pagination
          v-model:currentPage="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[2, 5, 10, 20]"
          :small="small"
          :disabled="disabled"
          :background="background"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totals"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>
    <div>
      <el-dialog
          v-model="dialogVisible"
          title="订单详情"
          width="30%">

          <el-descriptions
            model="form"
            class="margin-top"
            :column="1"
            border>
            <el-descriptions-item
              width="100px"
              align="center">
              <template #label>
                <div class="cell-item">
                  <el-icon :style="iconStyle">
                    <List />
                  </el-icon>
                  订单号
                </div>
              </template>
              {{ this.form.id }}
            </el-descriptions-item>

            <el-descriptions-item
              width="100px"
              align="center">
              <template #label>
                <div class="cell-item">
                  <el-icon :style="iconStyle">
                    <Tools />
                  </el-icon>
                  送物机器人
                </div>
              </template>
              {{ this.form.robot_name }}
            </el-descriptions-item>

            <el-descriptions-item
              width="100px"
              align="center">
              <template #label>
                <div class="cell-item">
                  <el-icon :style="iconStyle">
                    <UserFilled />
                  </el-icon>
                  发件人
                </div>
              </template>
              {{ this.form.sender }}
            </el-descriptions-item>

            <el-descriptions-item
              width="100px"
              align="center">
              <template #label>
                <div class="cell-item">
                  <el-icon :style="iconStyle">
                    <InfoFilled />
                  </el-icon>
                  发件人id
                </div>
              </template>
              {{ this.form.senderid }}
            </el-descriptions-item>

            <el-descriptions-item
              width="100px"
              align="center">
              <template #label>
                <div class="cell-item">
                  <el-icon :style="iconStyle">
                    <LocationFilled />
                  </el-icon>
                  取货位置
                </div>
              </template>
              {{ this.form.src }}
            </el-descriptions-item>  

            <el-descriptions-item
              width="100px"
              align="center">
              <template #label>
                <div class="cell-item">
                  <el-icon :style="iconStyle">
                    <UserFilled />
                  </el-icon>
                  收件人
                </div>
              </template>
              {{ this.form.receiver }}
            </el-descriptions-item>  

            <el-descriptions-item
              width="100px"
              align="center">
              <template #label>
                <div class="cell-item">
                  <el-icon :style="iconStyle">
                    <InfoFilled />
                  </el-icon>
                  收件人id
                </div>
              </template>
              {{ this.form.receiverid }}
            </el-descriptions-item> 

            <el-descriptions-item
              width="100px"
              align="center">
              <template #label>
                <div class="cell-item">
                  <el-icon :style="iconStyle">
                    <LocationFilled />
                  </el-icon>
                  送货位置
                </div>
              </template>
              {{ this.form.dest }}
            </el-descriptions-item> 

            <el-descriptions-item
              width="100px"
              align="center">
              <template #label>
                <div class="cell-item">
                  <el-icon :style="iconStyle">
                    <CaretRight />
                  </el-icon>
                  订单状态  
                </div>
              </template>
              <p v-if="this.form.state == '120'">{{ this.state.state4 }}</p>
              <p v-if="this.form.state == '000'">{{ this.state.state1 }}</p>
              <p v-if="this.form.state == '100'">{{ this.state.state2 }}</p>
              <p v-if="this.form.state == '110'">{{ this.state.state3 }}</p>
              <p v-if="this.form.state == '121'">{{ this.state.state5 }}</p>
              <p v-if="this.form.state == '122'">{{ this.state.state6 }}</p>
            </el-descriptions-item> 

            <el-descriptions-item
              width="100px"
              align="center">
              <template #label>
                <div class="cell-item">
                  <el-icon :style="iconStyle">
                    <BellFilled />
                  </el-icon>
                  创建时间
                </div>
              </template>
              {{ this.form.create_time }}
            </el-descriptions-item>

          </el-descriptions>
        <template #footer>
                  <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">确定</el-button>
<!--                    <el-button type="primary" @click="addFormSave('ruleForm')">确定</el-button>-->
                  </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import {Search} from '@element-plus/icons-vue'

export default {
  name: 'OrderView',
  components: {
    Search
  },
  data() {
    return {
      search: '',
      totals: 0,
      currentPage: '',
      pageSize: 10,
      dialogVisible: false,
      tableData: [],
      pageNum: 1,
      id: "",
      sender: "",
      mutipleSelection: [],

      form: {
        id: '',
        sender: '',
        senderid: '',
        src: '',
        receiver: '',
        receiverid: '',
        dest: '',
        senderurl: '',
        receiverurl: '',
        robot_name: '',
        src_uuid: '',
        dest_uuid: '',
        create_time: '',
        state: '',
      },

      //状态集
      state: {
        state1: '订单未分配',
        state2: '机器人正在取货',
        state3: '机器人到达取货位置',
        state4: '机器人正在送货',
        state5: '机器人到达送货位置',
        state6: '订单完成'
      },

      // 表单校验
      rules: {

      }
    }
  },



  created() {
    //请求分页查询数据
    this.load()
  },
  mounted() {
    this.timer = setInterval(() => {
      this.load()
    }, 1000)
  },

  beforeUnmount() {
    clearInterval(this.timer)
  },

  methods: {
    // 加载初始数据
    load(){
      this.request.get("/delivery/manager/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          id: this.id,
          sender: this.sender
        }
      }).then(res => {
        console.log(res.records);
        this.tableData = res.records
        this.totals = res.total
      })
    },

    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      console.log(pageSize)
      this.load()
    },

    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      console.log(pageNum)
      this.load()
    },

    reset() {
      this.id = ""
      this.sender = ""
      this.load()
    },

    //删除数据按钮
    deleteOrder(orderId) {
      this.request.delete("/delivery/"+orderId,).then(res =>{
        if (res){
          this.$message({
            message: '删除成功o(*￣▽￣*)ブ',
            type: 'success'
          });
          this.load();
        }else {
          this.$message({
            message: '删除失败,,ԾㅂԾ,,',
            type: 'error'
          });
        }
      });
    },

    handleSelectionChange(val) {
      console.log(val)
      this.mutipleSelection = val
    },

    delBatch() {
      let ids = this.mutipleSelection.map(v => v.id)
      this.request.delete("delivery/del/batch", {data: ids}).then(res =>{
        if (res){
          this.$message({
            message: '批量删除成功o(*￣▽￣*)ブ',
            type: 'success'
          });
          this.load();
        }else {
          this.$message({
            message: '批量删除失败,,ԾㅂԾ,,',
            type: 'error'
          });
        }
      });
    },

    //编辑信息按钮
    OrderInfo(row) {
      this.form = JSON.parse(JSON.stringify(row));
      console.log(this.form.state)
      this.dialogVisible = true;
      this.load();
    },
    //导出数据按钮
    exp() {
      window.open("http://localhost:9090/delivery/export")
    },
    //导入数据按钮
    importOrder() {

    },
    // // 改变当前每页数据个数触发
    // handleSizeChange() {
    //     this.load();
    // },
    // // 改变当前页码触发
    // handleCurrentChange() {
    //     this.load();
    // }
  }
}
</script>

<style lang="css" scoped>
.inputDeep>>>.el-input__inner {
  border: 0;
}
</style>