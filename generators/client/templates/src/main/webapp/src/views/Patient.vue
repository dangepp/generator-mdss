<template>
  <div class='animated fadeIn'>
    <b-card>
    <div class="row justify-content-start" style="height: calc(100vh - 276px); margin: 0px">

      <div style="overflow-y: scroll;" class="col-auto">

        <div class="row" v-for="(cat, key) in categories" :key="key">
          <button :class="{'button-selected': currentKey===key, 'btn btn-outline-primary': true}" style="width: 100%;"
                  :disabled="disabledCats[key]" @click="setCategory(key)">
            {{key}}
          </button>
        </div>
      </div>
      <div class="col" style="overflow-y: scroll;">
        <div v-for="(cat, key) in categories" :key="key">
          <feature-tab :single="cat.exclusive" :enums="cat.values" :patient="patient" :property-name="key"
                             :disabled="disabledCats[key]" v-show="currentKey === key"/>
        </div>
      </div>
    </div>
    <div style="float: right;">
      <button @click="resetPatient" class="btn btn-primary">
        reset
      </button>
      <button class="btn btn-primary" @click="sendPatient">
        send
      </button>
    </div>
      <b-modal size="lg" title="Diagnoses" v-model="diagnosesModal" @ok="diagnosesModal = false">
        <b-table striped hover :items="diagnoses" :fields="diagnoseFields"></b-table>
      </b-modal>

    </b-card>
  </div>
</template>

<script>
import FeatureTab from '../components/FeatureTab.vue'
import config from '../config'

export default {
  name: 'patient',
  components: {
    'feature-tab': FeatureTab
  },
  data: function () {
    return {
      patient: this.buildPatient(config.categories),
      diagnoses: [],
      categories: config.categories,
      disabledCats: this.buildDependencies(config.categories, {}),
      currentCategory: null,
      currentKey: null,
      diagnosesModal: false,
      diagnoseFields: {
        'disease.id': {
          label: 'Id'
        },
        'disease.diseaseName': {
          label: 'Name'
        },
        'disease.overallFrequency': {
          label: 'Frequency'
        },
        similarity: {
          label: 'Similarity'
        },
        rank: {
          label: 'Rank'
        }
      }
    }
  },
  methods: {
    sendPatient () {
      this.$http.post('/api/patient', this.patient).then(
        response => {
          // get body data
          this.diagnoses = response.body
          this.diagnosesModal = true
        },
        response => {
          this.$toastr.e(response.body.message)
          // error callback
        }
      )
    },
    resetPatient () {
      this.patient = this.buildPatient(this.categories)
      this.rebuildDependencies()
    },
    buildPatient (cats) {
      let patient = {}
      for (const key in cats) {
        const category = cats[key]
        if (!category.exclusive) {
          patient[key] = []
        } else {
          patient[key] = null
        }
      }
      return patient
    },
    rebuildDependencies () {
      this.disabledCats = this.buildDependencies(this.categories, this.patient)
    },
    buildDependencies (categories, patient) {
      let disabledCats = {}
      for (const key in categories) {
        const category = categories[key]
        for (const valueKey in category.values) {
          const value = category.values[valueKey]
          if (typeof value === 'object') {
            let disabled = false
            if (category.exclusive) {
              disabled = disabled || patient[key] !== value.name
            } else {
              disabled = disabled || !patient[key] || patient[key].indexOf(value.name) === -1
            }
            if (disabled) {
              for (const dependencyKey in value.dependent) {
                const dependency = value.dependent[dependencyKey]
                disabledCats[dependency] = true
              }
            }
          }
        }
      }
      return disabledCats
    },
    setCategory (key) {
      this.currentCategory = this.categories[key]
      this.currentKey = key
    }
  },
  watch: {
    'patient': {
      handler: function (val, oldVal) {
        this.rebuildDependencies()
      },
      deep: true
    }
  }
}
</script>

<style>
  .button-selected {
    background-color: #20a8d8;
    color: white;
  }
  .dashboard-container {
    background-color: white;
  }
</style>
