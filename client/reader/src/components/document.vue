<template>
  <div class="document">
    <div class="title" @click="OnClickTitle()"><label>{{document.filename}}</label></div>
    <div class="content">
      <div class="content-left">
        <label v-for="line in document.contentLines"> {{line}}</label>
      </div>
      <div class="content-right">content right</div>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import axios from 'axios'

export default {
  name: 'document',
  props: ['id', 'profile'],
  data () {
    return {
      document: {
        filename: '',
        contentLines: '',

      }
    }
  },
  mounted: function () {
    this.OnClickTitle()
  },
  computed: {
    /*
    document () {
      let doc = this.$store.state.doc_list[this.$props.profile]
      console.log(this.$store.state)
      if (typeof doc === 'undefined' || doc === null) {
        doc = {
          title: 'Document not found'
        }
      }
      doc = {
        title: 'Document not found'
      }
      return doc
    }
    */
  },
  methods: {
    ...mapActions(['get_document_list']),
    OnClickTitle () {
      let docId = ''
      if (this.$props.profile) {
        docId = this.$props.profile
        this.$session.set('profile', docId)
      } else {
        docId = this.$session.get('profile')
      }

      let outerThis = this
      axios.get('/document/get?id=' + docId)
        .then(function (response) {
          outerThis.document = response.data
        })
        .catch(function (error) {
          console.log(error)
        })
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .title {
    position: relative;
    display: inline-block;
    width: 100%;
    height: 40px;
    text-align: center;
    background-color: bisque;
  }
  .title label {
    position: absolute;
    margin: auto;
  }
  .content {
    position: relative;
    width: 100%;
    height: 500px;
  }
  .content-left {
    position: relative;
    display: inline-block;
    width: 70%;
    height: 100%;
    padding: 0 5px;
    background-color: white;
  }
  .content-right {
    position: relative;
    display: inline-block;
    width: 25%;
    height: 100%;
    padding: 0 5px;
    background-color: white;
  }
  h1, h2 {
    font-weight: normal;
  }
  ul {
    list-style-type: none;
    padding: 0;
  }
  li {
    display: inline-block;
    margin: 0 10px;
  }
  a {
    color: #42b983;
  }

  .content-left label {
    display: inline-block;
    width: 100%;
    text-align: left;
    font-size: 20px;
    line-height: 33px;
    margin: 2px 0;
    background-color: coral;
  }
</style>
