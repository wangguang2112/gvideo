import React from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  BackHandler,
  ToastAndroid
} from 'react-native';

class HelloWorld extends React.Component {
  render() {
    return (
      <View style={styles.container}>
         <Text style={styles.item1}>1</Text>
         <Text style={styles.item2}>2</Text>
         <Text style={styles.item3}>3</Text>
         <Text style={styles.item4}>4</Text>
         <Text style={styles.item5}>5</Text>
         <Text style={styles.item6}>6</Text>
         <Text style={styles.item7}>7</Text>
         <Text style={styles.item8}>8</Text>
         <Text style={styles.item9}>9</Text>
      </View>
    )
  }
}
var styles = StyleSheet.create({
   container: {
          backgroundColor: "blue",
          flex: 1,
          justifyContent: "space-between",
          flexWrap: "wrap",
          flexDirection: "row",
      },
      item1: {
          color: "#fff",
          backgroundColor: "#000",
          height: 80,
          width: 80,
          textAlign: "center",
          textAlignVertical: "center",
          margin: 4,
      },
      item2: {
          color: "#fff",
          backgroundColor: "#000",
          height: 80,
          width: 80,
          textAlign: "center",
          textAlignVertical: "center",
          margin: 4,
      },
      item3: {
          color: "#fff",
          backgroundColor: "#000",
          height: 80,
          width: 80,
          textAlign: "center",
          textAlignVertical: "center",
          margin: 4,
      },
      item4: {
          color: "#fff",
          backgroundColor: "#000",
          height: 80,
          width: 80,
          textAlign: "center",
          textAlignVertical: "center",
          margin: 4,
          alignSelf: "flex-end"
      },
      item5: {
          color: "#fff",
          backgroundColor: "#000",
          height: 80,
          width: 80,
          textAlign: "center",
          textAlignVertical: "center",
          margin: 4,
      },
      item6: {
          color: "#fff",
          backgroundColor: "#000",
          height: 80,
          width: 80,
          textAlign: "center",
          textAlignVertical: "center",
          margin: 4,
      },
      item7: {
          color: "#fff",
          backgroundColor: "#000",
          height: 80,
          width: 80,
          textAlign: "center",
          textAlignVertical: "center",
          margin: 4,
      },
      item8: {
          color: "#fff",
          backgroundColor: "#000",
          height: 80,
          width: 80,
          textAlign: "center",
          textAlignVertical: "center",
          margin: 4,
      },
      item9: {
          color: "#fff",
          backgroundColor: "#000",
          height: 80,
          width: 80,
          textAlign: "center",
          textAlignVertical: "center",
          margin: 4,
      },
});

BackHandler.addEventListener('hardwareBackPress', () => {
     ToastAndroid.show('返回了', ToastAndroid.SHORT);
});
AppRegistry.registerComponent('MyReactNativeApp', () => HelloWorld);