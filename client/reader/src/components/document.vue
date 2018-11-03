<template>
  <div class="document">
    <div class="title" @click="OnClickTitle()"><label>{{document.title}}</label></div>
    <div class="content">
      <div class="content-left">content left</div>
      <div class="content-right">content right</div>
    </div>
    <div>this is bottom</div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
export default {
  name: 'document',
  props: ['id'],
  data () {
    return {
      msg: 'Welcome to Your Vue.js App'
    }
  },
  computed: {
    document () {
      let doc = this.$store.state.doc_list[this.$props.id]
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
  },
  methods: {
    OnClickTitle () {
      this.$store.dispatch('get_document_list')
    },
    ...mapActions(['get_document_list'])
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
    background-color: rebeccapurple;
  }
  .content-right {
    position: relative;
    display: inline-block;
    width: 25%;
    height: 100%;
    padding: 0 5px;
    background-color: rebeccapurple;
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
</style>
