<template>
<div class="row justify-content-around">
     <div :class="{'col-auto': true, 'feature-box': true, 'feature-selected': itemSelected(item)}" v-for="(item) in arrayData" :key="item" @click="setItem(item)">
       <img width="175" height="175" :src="getImageName(propertyName, item)"
            @error="noImageHandler(propertyName, item, $event)" alt="Responsive image"/>
       <br/>
      <label>{{item}}</label>
      <br>
    </div>
</div>
</template>
<script>
import ImageUtil from '../util/ImageUtil'

export default {
  name: 'feature-tab',
  props: {
    enums: {
      type: Array,
      required: true
    },
    patient: {
      type: Object,
      required: true
    },
    propertyName: {
      type: String,
      required: true
    },
    disabled: {
      required: true
    },
    single: {
      type: Boolean,
      default: false
    }
  },
  data: function () {
    let arrayData = []
    let featureImages = {}
    this.enums.forEach(element => {
      featureImages[ImageUtil.getImageIdentifier(this.propertyName, element)] = false
      arrayData.push(ImageUtil.getFeatureName(element))
    })
    return {
      arrayData: arrayData,
      imageUtil: ImageUtil,
      featureImages: featureImages
    }
  },
  methods: {
    setItem (item) {
      if (this.single) {
        this.patient[this.propertyName] = item
      } else {
        let index = this.patient[this.propertyName].indexOf(item)
        if (index > -1) {
          this.patient[this.propertyName].splice(index, 1)
        } else {
          this.patient[this.propertyName].push(item)
        }
      }
    },
    itemSelected (item) {
      if (this.single) {
        return this.patient[this.propertyName] === item
      } else {
        return this.patient[this.propertyName].indexOf(item) > -1
      }
    },
    getImageName (key, item) {
      return this.imageUtil.getImageName(this.featureImages, key, item)
    },
    noImageHandler (key, item, event) {
      this.imageUtil.noImageHandler(this.featureImages, key, item, event)
    }
  },
  watch: {
    'disabled': {
      handler: function (val, oldVal) {
        if (val) {
          if (this.patient[this.propertyName]) {
            if (!this.single) {
              this.patient[this.propertyName].splice(0, this.patient[this.propertyName].length)
            } else {
              this.patient[this.propertyName] = null
            }
          }
        }
      }
    }
  }
}
</script>
<style>
  .feature-selected {
    background-color: #20a8d8;
    color: white;
  }

  .feature-box {
    padding-left: 28px;
    padding-right: 28px;
    padding-top: 28px;
  }
</style>
