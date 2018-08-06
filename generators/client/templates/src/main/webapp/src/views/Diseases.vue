<template>
  <div class='animated fadeIn'>
    <b-card header="Disease File Upload">
      <input type="file" :disabled="isSaving" @change="fileChange" accept=".xls">
      <button @click="uploadFile" :disabled="!file || isSaving">
        Upload
      </button>
    </b-card>

    <b-card header-tag="header">
      <div slot="header" class="row header justify-content-between">
        <div>
          Feature Picture Management
        </div>
        <div>
          <button @click="collapseAllFeatures()">
            Collapse all
          </button>
      </div>
      </div>
      <div class="category-box" v-for="(cat, key) in categories" :key="key"
           @click="cat.showCollapse = !cat.showCollapse">
        <label >{{key}}</label>
      <b-collapse :id="key" v-model="cat.showCollapse">
        <div class="row justify-content-around">
        <div class="col-auto image-box" v-for="(item) in cat.values" :key="imageUtil.getImageIdentifier(key, item)">
          <input :disabled="isSaving" :ref="imageUtil.getImageIdentifier(key, item)"
                 :id="imageUtil.getImageIdentifier(key, item)"
                 type="file" @change="uploadFeature" v-show="false" accept=".png"/>
          <img width="175" height="175" :src="getImageName(key, item)"
               @error="noImageHandler(key, item, $event)" alt="Responsive image"
          @click="openImageSelector(key, item)"/>
          <br/>
          <label>{{imageUtil.getFeatureName(item)}}</label>
        </div>
        </div>
      </b-collapse>
      </div>
    </b-card>
  </div>
</template>

<script>
import config from '../config'
import ImageUtil from '../util/ImageUtil'

export default {
  name: 'diseases',
  data: function () {
    return {
      categories: config.categories,
      isSaving: false,
      file: null,
      featureImages: {},
      imageUtil: ImageUtil
    }
  },
  methods: {
    fileChange (e) {
      if (e.target.files.length > 0) {
        this.file = e.target.files[0]
      } else {
        this.file = null
      }
    },
    uploadFile () {
      this.isSaving = true
      let formData = new FormData()
      formData.append('excelFile', this.file)
      this.$http.post('/api/diseases/xls', formData).then((response) => {
        console.log('File sent...') // this block is never triggered
        console.log(response)
        this.$toastr.s('Successfully saved diseases!')
        this.isSaving = false
      }, (response) => {
        console.log('Error occurred...')
        this.$toastr.e(response.body.message)
        this.isSaving = false
      })
    },
    uploadFeature (event) {
      if (event.target.files.length > 0) {
        let file = event.target.files[0]
        this.isSaving = true
        let formData = new FormData()
        formData.append('featureFile', file)
        formData.append('fileName', event.target.id + '.png')
        this.$http.put('/feature', formData).then((response) => {
          this.$toastr.s('Successfully saved feature image!')
          this.featureImages[event.target.id] = false
          this.isSaving = false
        }, (response) => {
          console.log('Error occurred...')
          this.$toastr.e(response.body.message)
          this.isSaving = false
        })
      }
    },
    getImageName (key, item) {
      return this.imageUtil.getImageName(this.featureImages, key, item)
    },
    noImageHandler (key, item, event) {
      this.imageUtil.noImageHandler(this.featureImages, key, item, event)
    },
    openImageSelector (key, item) {
      const selector = this.$refs[this.imageUtil.getImageIdentifier(key, item)]
      // todo why is this an array??
      // todo endpoint for image to upload and then save in static?
      selector[0].click()
    },
    collapseAllFeatures () {
      for (let cat in this.categories) {
        this.categories[cat].showCollapse = false
      }
    }
  }
}
</script>
<style>
  .image-box {
    border:1px solid black;
    padding-top: 15px;
    margin-bottom:8px
  }
  .category-box {
    border-bottom: 1px solid black;
  }
  .row.header {
    margin: 0;
  }
</style>
