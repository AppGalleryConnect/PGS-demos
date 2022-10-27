module.exports = {
  env: {
  browser: true,
  node: true,
  es6: true,
  },
  extends: ['eslint:recommended',"plugin:@typescript-eslint/recommended",'@appstore/eslint-config-huawei'],
  parserOptions: {
    requireConfigFile: false,
    parser: '@babel/eslint-parser',
    sourceType: 'module',
  },
  plugins: [],
  rules: {
  },
  globals:{
    cc : true,
    GOBE: true,
    qg:true
  },
};
