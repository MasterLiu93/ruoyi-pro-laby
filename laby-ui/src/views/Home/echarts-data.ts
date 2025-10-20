import { EChartsOption } from 'echarts'

const { t } = useI18n()

export const lineOptions: EChartsOption = {
  title: {
    text: t('analysis.monthlySales'),
    left: 'center'
  },
  xAxis: {
    data: [
      t('analysis.january'),
      t('analysis.february'),
      t('analysis.march'),
      t('analysis.april'),
      t('analysis.may'),
      t('analysis.june'),
      t('analysis.july'),
      t('analysis.august'),
      t('analysis.september'),
      t('analysis.october'),
      t('analysis.november'),
      t('analysis.december')
    ],
    boundaryGap: false,
    axisTick: {
      show: false
    }
  },
  grid: {
    left: 20,
    right: 20,
    bottom: 20,
    top: 80,
    containLabel: true
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    },
    padding: [5, 10]
  },
  yAxis: {
    axisTick: {
      show: false
    }
  },
  legend: {
    data: [t('analysis.estimate'), t('analysis.actual')],
    top: 50
  },
  series: [
    {
      name: t('analysis.estimate'),
      smooth: true,
      type: 'line',
      data: [100, 120, 161, 134, 105, 160, 165, 114, 163, 185, 118, 123],
      animationDuration: 2800,
      animationEasing: 'cubicInOut'
    },
    {
      name: t('analysis.actual'),
      smooth: true,
      type: 'line',
      itemStyle: {},
      data: [120, 82, 91, 154, 162, 140, 145, 250, 134, 56, 99, 123],
      animationDuration: 2800,
      animationEasing: 'quadraticOut'
    }
  ]
}

export const pieOptions: EChartsOption = {
  tooltip: {
    trigger: 'item',
    formatter: '<b>{b}</b><br/>访问量: {c}<br/>占比: {d}%',
    backgroundColor: 'rgba(255, 255, 255, 0.96)',
    borderColor: 'transparent',
    textStyle: {
      color: '#374151',
      fontSize: 13
    },
    padding: [12, 16],
    extraCssText: 'box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); border-radius: 8px;'
  },
  legend: {
    orient: 'vertical',
    right: 20,
    top: 'center',
    itemWidth: 14,
    itemHeight: 14,
    itemGap: 16,
    icon: 'circle',
    textStyle: {
      fontSize: 13,
      color: '#6b7280',
      padding: [0, 0, 0, 8]
    },
    formatter: function(name) {
      return name
    },
    data: [
      t('analysis.searchEngines'),
      t('analysis.directAccess'),
      t('analysis.allianceAdvertising'),
      t('analysis.mailMarketing'),
      t('analysis.videoAdvertising')
    ]
  },
  graphic: [
    {
      type: 'text',
      left: 'center',
      top: '43%',
      style: {
        text: '总访问量',
        fill: '#9ca3af',
        fontSize: 13,
        fontWeight: 500
      } as any
    },
    {
      type: 'text',
      left: 'center',
      top: '50%',
      style: {
        text: '3,556',
        fill: '#1f2937',
        fontSize: 26,
        fontWeight: 700
      } as any
    }
  ],
  series: [
    {
      name: t('analysis.userAccessSource'),
      type: 'pie',
      radius: ['55%', '75%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 6,
        borderColor: '#fff',
        borderWidth: 3
      },
      label: {
        show: false
      },
      emphasis: {
        scale: true,
        scaleSize: 8,
        itemStyle: {
          shadowBlur: 20,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.3)'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { 
          value: 1856, 
          name: t('analysis.searchEngines'), 
          itemStyle: { 
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#10b981' },
                { offset: 1, color: '#059669' }
              ]
            }
          } 
        },
        { 
          value: 782, 
          name: t('analysis.directAccess'), 
          itemStyle: { 
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#6366f1' },
                { offset: 1, color: '#4f46e5' }
              ]
            }
          } 
        },
        { 
          value: 456, 
          name: t('analysis.allianceAdvertising'), 
          itemStyle: { 
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#f59e0b' },
                { offset: 1, color: '#d97706' }
              ]
            }
          } 
        },
        { 
          value: 298, 
          name: t('analysis.mailMarketing'), 
          itemStyle: { 
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#ec4899' },
                { offset: 1, color: '#db2777' }
              ]
            }
          } 
        },
        { 
          value: 164, 
          name: t('analysis.videoAdvertising'), 
          itemStyle: { 
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#8b5cf6' },
                { offset: 1, color: '#7c3aed' }
              ]
            }
          } 
        }
      ],
      animationType: 'scale',
      animationEasing: 'elasticOut',
      animationDelay: function (idx) {
        return idx * 100
      }
    }
  ]
}

export const barOptions: EChartsOption = {
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'none'
    },
    backgroundColor: 'rgba(255, 255, 255, 0.96)',
    borderColor: 'transparent',
    textStyle: {
      color: '#374151',
      fontSize: 13
    },
    padding: [12, 16],
    extraCssText: 'box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); border-radius: 8px;',
    formatter: function(params: any) {
      const item = params[0]
      return `<b>${item.name}</b><br/>活跃量: <span style="color:#6366f1;font-weight:600;">${item.value.toLocaleString()}</span>`
    }
  },
  grid: {
    left: 60,
    right: 30,
    bottom: 40,
    top: 40,
    containLabel: false
  },
  xAxis: {
    type: 'category',
    data: [
      t('analysis.monday'),
      t('analysis.tuesday'),
      t('analysis.wednesday'),
      t('analysis.thursday'),
      t('analysis.friday'),
      t('analysis.saturday'),
      t('analysis.sunday')
    ],
    axisTick: {
      show: false
    },
    axisLine: {
      show: false
    },
    axisLabel: {
      color: '#6b7280',
      fontSize: 12,
      fontWeight: 500,
      margin: 12
    }
  },
  yAxis: {
    type: 'value',
    splitLine: {
      lineStyle: {
        color: '#f3f4f6',
        type: 'solid',
        width: 1
      }
    },
    axisLine: {
      show: false
    },
    axisTick: {
      show: false
    },
    axisLabel: {
      color: '#9ca3af',
      fontSize: 11,
      formatter: function(value: number) {
        if (value >= 1000) {
          return (value / 1000).toFixed(0) + 'k'
        }
        return value.toString()
      }
    }
  },
  series: [
    {
      name: t('analysis.activeQuantity'),
      data: [8642, 12356, 15234, 18967, 16543, 5621, 4893],
      type: 'bar',
      barWidth: '45%',
      itemStyle: {
        color: function(params: any) {
          // 为每个柱子设置不同的渐变色
          const colors = [
            ['#6366f1', '#8b5cf6'],
            ['#8b5cf6', '#a855f7'],
            ['#a855f7', '#c026d3'],
            ['#c026d3', '#d946ef'],
            ['#a855f7', '#8b5cf6'],
            ['#8b5cf6', '#6366f1'],
            ['#6366f1', '#4f46e5']
          ]
          const colorSet = colors[params.dataIndex] || colors[0]
          return {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: colorSet[0] },
              { offset: 1, color: colorSet[1] }
            ]
          }
        },
        borderRadius: [10, 10, 0, 0],
        shadowColor: 'rgba(99, 102, 241, 0.3)',
        shadowBlur: 8,
        shadowOffsetY: 4
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 15,
          shadowColor: 'rgba(99, 102, 241, 0.5)',
          shadowOffsetY: 8
        }
      },
      animationDelay: function (idx: number) {
        return idx * 80
      }
    }
  ],
  animationEasing: 'elasticOut',
  animationDelayUpdate: function (idx: number) {
    return idx * 50
  }
}

export const radarOption: EChartsOption = {
  legend: {
    data: [t('workplace.personal'), t('workplace.team')]
  },
  radar: {
    // shape: 'circle',
    indicator: [
      { name: t('workplace.quote'), max: 65 },
      { name: t('workplace.contribution'), max: 160 },
      { name: t('workplace.hot'), max: 300 },
      { name: t('workplace.yield'), max: 130 },
      { name: t('workplace.follow'), max: 100 }
    ]
  },
  series: [
    {
      name: `xxx${t('workplace.index')}`,
      type: 'radar',
      data: [
        {
          value: [42, 30, 20, 35, 80],
          name: t('workplace.personal')
        },
        {
          value: [50, 140, 290, 100, 90],
          name: t('workplace.team')
        }
      ]
    }
  ]
}

export const wordOptions = {
  series: [
    {
      type: 'wordCloud',
      gridSize: 2,
      sizeRange: [12, 50],
      rotationRange: [-90, 90],
      shape: 'pentagon',
      width: 600,
      height: 400,
      drawOutOfBound: true,
      textStyle: {
        color: function () {
          return (
            'rgb(' +
            [
              Math.round(Math.random() * 160),
              Math.round(Math.random() * 160),
              Math.round(Math.random() * 160)
            ].join(',') +
            ')'
          )
        }
      },
      emphasis: {
        textStyle: {
          shadowBlur: 10,
          shadowColor: '#333'
        }
      },
      data: [
        {
          name: 'Sam S Club',
          value: 10000,
          textStyle: {
            color: 'black'
          },
          emphasis: {
            textStyle: {
              color: 'red'
            }
          }
        },
        {
          name: 'Macys',
          value: 6181
        },
        {
          name: 'Amy Schumer',
          value: 4386
        },
        {
          name: 'Jurassic World',
          value: 4055
        },
        {
          name: 'Charter Communications',
          value: 2467
        },
        {
          name: 'Chick Fil A',
          value: 2244
        },
        {
          name: 'Planet Fitness',
          value: 1898
        },
        {
          name: 'Pitch Perfect',
          value: 1484
        },
        {
          name: 'Express',
          value: 1112
        },
        {
          name: 'Home',
          value: 965
        },
        {
          name: 'Johnny Depp',
          value: 847
        },
        {
          name: 'Lena Dunham',
          value: 582
        },
        {
          name: 'Lewis Hamilton',
          value: 555
        },
        {
          name: 'KXAN',
          value: 550
        },
        {
          name: 'Mary Ellen Mark',
          value: 462
        },
        {
          name: 'Farrah Abraham',
          value: 366
        },
        {
          name: 'Rita Ora',
          value: 360
        },
        {
          name: 'Serena Williams',
          value: 282
        },
        {
          name: 'NCAA baseball tournament',
          value: 273
        },
        {
          name: 'Point Break',
          value: 265
        }
      ]
    }
  ]
}
