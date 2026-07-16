import { onMounted } from 'vue'

const DEFAULT_TITLE = 'Xiangzi AI Agent | 智能AI助手平台'
const DEFAULT_DESC = 'Xiangzi AI Agent - 新一代智能AI助手平台，提供AI恋爱大师、AI超级智能体等前沿AI应用'

export function useSEO({ title, description } = {}) {
  onMounted(() => {
    // 设置标题
    document.title = title ? `${title} | Xiangzi AI` : DEFAULT_TITLE

    // 设置 description meta
    let metaDesc = document.querySelector('meta[name="description"]')
    if (!metaDesc) {
      metaDesc = document.createElement('meta')
      metaDesc.setAttribute('name', 'description')
      document.head.appendChild(metaDesc)
    }
    metaDesc.setAttribute('content', description || DEFAULT_DESC)

    // 设置 og:title
    let ogTitle = document.querySelector('meta[property="og:title"]')
    if (!ogTitle) {
      ogTitle = document.createElement('meta')
      ogTitle.setAttribute('property', 'og:title')
      document.head.appendChild(ogTitle)
    }
    ogTitle.setAttribute('content', title ? `${title} | Xiangzi AI` : DEFAULT_TITLE)

    // 设置 og:description
    let ogDesc = document.querySelector('meta[property="og:description"]')
    if (!ogDesc) {
      ogDesc = document.createElement('meta')
      ogDesc.setAttribute('property', 'og:description')
      document.head.appendChild(ogDesc)
    }
    ogDesc.setAttribute('content', description || DEFAULT_DESC)
  })
}
