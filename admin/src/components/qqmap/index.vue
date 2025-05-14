<template>
    <tmap-map
      :mapKey="tencentMap"
      :events="events"
      :center="center"
      :zoom="zoom"
      :doubleClickZoom="doubleClickZoom"
      :control="control"
    >
        <tmap-multi-marker
        :styles="markerStyles"
        :geometries="markerGeometries"
        ref="markers"
        />
    </tmap-map>
  </template>
  
  <script lang="ts">
    import { defineComponent, ref, defineProps } from 'vue';
    import iconMarker from '@/assets/images/icon_marker.png'
    import { clone, isEmpty } from "@/utils/util"

    export default defineComponent({
      name: 'qqmap-index',
      props: {
        tencentMap: {
            type: String,
            default: ''
        },
        extendData: {
            type: Array,
            default: () => {
                return []
            }
        },
        centerLocation: {
            type: Object,
            default: () => {
                return undefined
            }
        }
      },
      emits: ['click'],
      setup(props, { emit }) {
        //const props = defineProps(["tencentMap"])
        const center = ref({ lat: 39.98481500648338, lng: 116.30571126937866 });
        const zoom = ref(15);
        const doubleClickZoom = ref(false);
        const print = (e: unknown) => {
          console.log(e);
        };
        const clickData = (e: any) => {
            //console.log(e)
            markerGeometries.value = [
                {
                    id: 'start',
                    styleId: 'me',
                    position: { lat: e.latLng.lat, lng: e.latLng.lng },
                }
            ]
            
            if (props.extendData.length > 0) {
                props.extendData.map(item => {
                    markerGeometries.value.push(item)
                })
            }
            emit('click', e)
            //console.log(props.extendData)
        }
        const markerStyles = ref({
                me: {
                    width: 34,
                    height: 34,
                    anchor: { x: 16, y: 32 },
                    src: iconMarker,
                },
                // marker: {
                //     width: 34,
                //     height: 34,
                //     anchor: { x: 16, y: 32 },
                // },
        })
        const markerGeometries = ref([
            {
                id: 'start',
                styleId: 'me',
                position: { lat: 39.98481500648338, lng: 116.30571126937866 },
            }
        ])

        watch(
            () => props.extendData,
            (newValue, oldValue) => {
                //let cPmarkerGeometries = clone(markerGeometries.value)
                //console.log(cPmarkerGeometries[0])
                markerGeometries.value = []
                //markerGeometries.value.push(cPmarkerGeometries[0])
                if (props.extendData.length > 0) {
                    props.extendData.map((item, index) => {
                        if (index == 0) {
                            markerGeometries.value.push({
                                id: 'start',
                                styleId: 'me',
                                position: { lat: item.position.lat, lng: item.position.lng },
                                content: item.content,
                                properties: {
                                    title: item.content
                                },
                            })
                        } else {
                            markerGeometries.value.push(item)
                        }
                    })
                }
                center.value = { lat: markerGeometries.value[0].position.lat, lng: markerGeometries.value[0].position.lng }
            }
        );

        watch (
            () => props.centerLocation,
            (newVal, oldVal) => {
                if (isEmpty(newVal) == false) {
                    center.value = {
                        lat: newVal.lat, lng: newVal.lng
                    }
                    markerGeometries.value = []
                    markerGeometries.value.push({
                        id: 'start',
                        styleId: 'me',
                        position: { lat: newVal.lat, lng: newVal.lng },
                        content: newVal.content,
                        properties: {
                            title: newVal.content
                        },
                    })
                }
               
            },
            { deep: true }
        )

        return {
          events: {
            dblclick: print,
            click: clickData
          },
          center,
          zoom,
          doubleClickZoom,
          markerGeometries,
          markerStyles,
          control: {
            scale: {},
            zoom: {
              position: 'bottomRight',
            },
          },
        };
      },
    });
  </script>