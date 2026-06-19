import {useContext} from 'react';
import {MdEditor} from 'md-editor-rt';
import {ExportPDF, Mark} from '@vavt/rt-extension';
import {PostEditorContext} from '../context/PostEditorContext.tsx';
import {message} from 'antd';

export default function PostMarkdownEditor() {
    const {
        pid,
        post, setPost,
        editorContent, setEditorContent,
        setDraft, removeDraft
    } = useContext(PostEditorContext);

    return (
        <MdEditor id={'post_editor'}
                  placeholder='开始记录奇思妙想...'
                  toolbars={[
                      // 第一组图标
                      0, 'bold', 'underline', 'italic', 'strikeThrough', '-',
                      // 第二组图标 "-" 是分隔符
                      'quote', 'unorderedList', 'orderedList', '-',
                      // 第三组图标 "-" 是分隔符
                      'task', 'codeRow', 'code', 'image', 'table', 'mermaid', 'katex', '-',
                      // 第四组图标
                      'revoke', 'next', 'save',
                      // 第三组图标 "=" 是右对齐  "-" 是分隔符
                      '=', '-', 'prettier', 'previewOnly', 1, 'pageFullscreen', 'catalog',
                  ]}
                  defToolbars={[
                      <Mark title={'高亮'} key='Mark'/>,
                      <ExportPDF key='ExportPDF' value={editorContent}/>,
                  ]}
                  value={editorContent}
                  onChange={(value) => {
                      setEditorContent(value);
                  }}
                  autoFocus={true}
                  noPrettier={false}
                  showToolbarName={true}
                  onSave={(content) => {
                      if (content == null || content.length < 1) {
                          removeDraft(pid);
                          message.warning('已将本地草稿清空！');
                      } else {
                          setDraft(content, pid);
                          message.info({content: '已将草稿保存到本地。'});
                      }
                  }}
                  onUploadImg={undefined}
        />
    );
}
