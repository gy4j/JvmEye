export function getContentWithDeep(deep) {
  let content = "";
  for (let i = 0; i < deep; i++) {
    content = content + "----";
  }
  return content;
}