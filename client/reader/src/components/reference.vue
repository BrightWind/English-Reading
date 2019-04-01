<template>
  <div class="main-frame">
    <div class="doc-list">
      <li v-for="doc in doc_list" @click="OnClickItem(doc)">
        <div class="doc-item">
          <div class="doc-desc">{{doc.fileName}}</div>
        </div>
      </li>

      <router-link :to="{ name: 'document', params: { id: 123 }}">bar</router-link>
    </div>
    <div>
      <footer></footer>
    </div>
  </div>
</template>
<script>
import {mapState, mapActions} from 'vuex'
export default {
  data () {
    return {
      docs: [{title: 'Start learning document'}, {title: 'Start learning English'}]
    }
  },
  mounted () {
    this.load_doc_list()
  },
  computed: {
    ...mapState(['doc_list'])
  },
  methods: {
    OnClickItem (item) {
      console.log(item);
      this.$router.push({name: 'document', params: { id: item.id, profile: item }})
    },
    ...mapActions(
      {
        load_doc_list: 'init'
      }
    )
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  h1, h2 {
    font-weight: normal;
  }
  ul {
    list-style-type: none;
    padding: 0;
  }
  li {
    list-style-type: none;
  }
  .doc-list {
    padding-top: 12px;
  }
  .doc-item {
    position: relative;
    display: inline-block;
    margin: 5px 10px;
    height: 40px;
    width: 80%;
    background-color: white;
  }
  .doc-desc {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    margin: auto 10px;
    height: 20px;
    width: 70%;
    font-size: 20px;
    text-align: left;
  }
  a {
    color: #42b983;
  }

  .main-frame {
    width: 100%;
    //height: 1000px;
    background-color: bisque;
    //margin-top: 50px;
  }
</style>
